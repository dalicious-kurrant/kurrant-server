package co.dalicious.data.redis.pubsub;

import co.dalicious.data.redis.dto.SseReceiverDto;
import co.dalicious.data.redis.dto.SseResponseDto;
import co.dalicious.data.redis.entity.NotificationHash;
import co.dalicious.data.redis.repository.EmitterRepository;
import co.dalicious.data.redis.repository.NotificationHashRepository;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30;
    private final EmitterRepository emitterRepository;
    private final NotificationHashRepository notificationHashRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisTemplate redisTemplate;

    public SseEmitter subscribe(BigInteger userId, String lastEventId) {

        //구독한 유저를 특정하기 위한 id.
        String id = String.valueOf(userId);

        //생성한 emitter를 저장한다. emitter는 HTTP/2기준 브라우저 당 100개 만들 수 있다.
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id, emitter);
        System.out.println("emitter = " + emitter);
        MessageListener messageListener = (message, pattern) -> {
            try {
                System.out.println("Received message from Redis on pattern: " + pattern);
                sendToClient(emitter, id, message);
            } catch(Exception e) {
                e.printStackTrace();
            }
        };

        redisMessageListenerContainer.addMessageListener(messageListener, ChannelTopic.of(getChannelName(id)));

        //기존 emitter 중 완료 되거나 시간이 초과되어 연결이 끊긴 emitter를 삭제한다.
        checkEmitterStatus(emitter, id, messageListener);

        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
            emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(userId));
        }

        return emitter;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void send(SseReceiverDto sseReceiverDto) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        NotificationHash notification = createNotification(sseReceiverDto, today);
        notificationHashRepository.save(notification);

        String id = String.valueOf(sseReceiverDto.getReceiver());
        emitterRepository.saveEventCache(id, notification);
        redisTemplate.convertAndSend(getChannelName(id), notification);
    }

    //notification 생성
    public NotificationHash createNotification(SseReceiverDto sseReceiverDto, LocalDate today) {
        return NotificationHash.builder()
                .type(sseReceiverDto.getType())
                .userId(sseReceiverDto.getReceiver())
                .isRead(false)
                .content(sseReceiverDto.getContent())
                .createDate(today)
                .groupId(sseReceiverDto.getGroupId())
                .commentId(sseReceiverDto.getCommentId())
                .noticeId(sseReceiverDto.getNoticeId())
                .build();
    }

    //client에게 이벤트 보내기
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            log.info("data = " + data.toString());
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("message")
                    .data(data));
        } catch (IOException exception) {
            log.info("exception = " + exception);
            emitterRepository.deleteAllEmitterStartWithId(id);
        }
    }

    @Transactional
    public void readNotification(BigInteger userId, Integer type, List<String> ids) {
        List<NotificationHash> notificationList;
        if(ids == null || ids.isEmpty()) notificationList = notificationHashRepository.findAllByUserIdAndTypeAndIsRead(userId, type, false);
        else notificationList = (List<NotificationHash>) notificationHashRepository.findAllById(ids);

        //읽을 알림이 있는지 확인
        if(notificationList.size() == 0) {
            log.info("읽을 알림이 없습니다.");
            return;
        }

        // 알림 읽기
        for(NotificationHash noty : notificationList) {
            noty.updateRead(true);
            notificationHashRepository.save(noty);
        }
        log.info("알림 읽기 성공");
    }

    @Transactional(readOnly = true)
    public List<SseResponseDto> getAllNotification(BigInteger userId) {
        List<NotificationHash> notificationList = notificationHashRepository.findAllByUserIdAndIsRead(userId, false);

        List<SseResponseDto> sseResponseDtos = new ArrayList<>();

        if(notificationList.isEmpty()) return sseResponseDtos;

        sseResponseDtos = notificationList.stream().map(v -> {
            SseResponseDto sseResponseDto = new SseResponseDto();

            sseResponseDto.setId(v.getId());
            sseResponseDto.setType(v.getType());
            sseResponseDto.setContent(v.getContent());
            sseResponseDto.setCreateDate(DateUtils.localDateToString(v.getCreateDate()));
            sseResponseDto.setIsRead(false);
            sseResponseDto.setGroupId(v.getGroupId());
            sseResponseDto.setCommentId(v.getCommentId());
            sseResponseDto.setNoticeId(v.getNoticeId());

            return sseResponseDto;

        }).collect(Collectors.toList());

        return sseResponseDtos;
    }

    private void checkEmitterStatus(final SseEmitter emitter,final String id, final MessageListener messageListener) {
        emitter.onCompletion(() -> {
            emitterRepository.deleteById(id);
            redisMessageListenerContainer.removeMessageListener(messageListener);
        });
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(id);
            redisMessageListenerContainer.removeMessageListener(messageListener);
        });
        emitter.onError((error) -> {
            System.out.println("error = " + error);
            emitterRepository.deleteById(id);
            redisMessageListenerContainer.removeMessageListener(messageListener);
        });
    }

    private String getChannelName(final String userId) {
        return "appTopics:" + userId;
    }
}
