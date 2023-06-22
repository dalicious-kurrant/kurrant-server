package co.dalicious.client.sse;

import co.dalicious.data.redis.entity.NotificationHash;
import co.dalicious.data.redis.repository.NotificationHashRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30;
    private final EmitterRepository emitterRepository;
    private final NotificationHashRepository notificationHashRepository;

    public SseEmitter subscribe(BigInteger userId, String lastEventId) {
        //구독한 유저를 특정하기 위한 id.
        String id = userId + "_" + System.currentTimeMillis();

        //생성한 emitter를 저장한다. emitter는 HTTP/2기준 브라우저 당 100개 만들 수 있다.
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        //기존 emitter 중 완료 되거나 시간이 초과되어 연결이 끊긴 emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 503 에러를 방지하기 위한 더미 이벤트 전송. 연결 중 한 번도 이벤트를 보낸 적이 없다면 다음 연결 때 503에러를 낸다.
        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    @Transactional
    public void send(BigInteger receiver, Integer type, String content) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        NotificationHash notification = createNotification(receiver, type, content, today);
        String id = String.valueOf(receiver);

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        if(sseEmitters.isEmpty()) return;
        System.out.println("sseEmitters = " + sseEmitters);
        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, notification);
                }
        );
        notificationHashRepository.save(notification);
    }

    //notification 생성
    private NotificationHash createNotification(BigInteger receiverId, Integer type, String content, LocalDate today) {
        return NotificationHash.builder()
                .type(type)
                .userId(receiverId)
                .isRead(false)
                .content(content)
                .createDate(today)
                .build();
    }

    //client에게 이벤트 보내기
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("message")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteAllEmitterStartWithId(id);
            throw new ApiException(ExceptionEnum.CONNECTION_ERROR);
        }
    }

    @Transactional
    public void readNotification(BigInteger userId, Integer type) {
        List<NotificationHash> notificationList =
                notificationHashRepository.findAllByUserIdAndTypeAndIsRead(userId, type, false);

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
    public Integer getAllNotification(BigInteger userId, Integer type) {
        List<NotificationHash> notificationList = notificationHashRepository.findAllByUserIdAndTypeAndIsRead(userId, type, false);

        //읽을 알림이 있는지 확인
        if(notificationList.size() == 0) { throw new ApiException(ExceptionEnum.ALREADY_READ); }

        //읽을 알림의 갯수 보내기
        return notificationList.size();
    }
}
