package co.dalicious.data.redis.pubsub;

import co.dalicious.data.redis.event.ReloadEvent;
import co.dalicious.data.redis.repository.EmitterRepository;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class SseEventService {
    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30;
    private final EmitterRepository emitterRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public SseEmitter subscribe(BigInteger makersId, String lastEventId) {
        String id = String.valueOf(makersId);

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        //기존 emitter 중 완료 되거나 시간이 초과되어 연결이 끊긴 emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        emitterRepository.save(id, emitter);

        // Sending the initial message to the client immediately.
        sendToClient(emitter, id, "EventStream Created. [makersId=" + makersId + "]");

        // MessageListener that reacts to Redis messages.
        MessageListener messageListener = (message, pattern) -> {
            try {
                System.out.println("Received message from Redis on pattern: " + pattern);
                sendToClient(emitter, id, message.toString());
            } catch(Exception e) {
                e.printStackTrace();
            }
        };

        redisMessageListenerContainer.addMessageListener(messageListener, ChannelTopic.of(getChannelName(id)));

        checkEmitterStatus(emitter, messageListener);

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        System.out.println("emitter = " + emitter);
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("message")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteAllEmitterStartWithId(id);
            System.err.println("Error sending message to client: " + exception.getMessage());
            throw new ApiException(ExceptionEnum.CONNECTION_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void send(ReloadEvent reloadEvent) {
        String notification = "Reload! " + "(" + DateUtils.localDateTimeToString(LocalDateTime.now()) + ")";

        for (BigInteger receiver : reloadEvent.getMakersIds()) {
            String id = String.valueOf(receiver);
            stringRedisTemplate.convertAndSend(getChannelName(id), notification);
        }
    }

    private void checkEmitterStatus(final SseEmitter emitter, final MessageListener messageListener) {
        emitter.onCompletion(() -> redisMessageListenerContainer.removeMessageListener(messageListener));
        emitter.onTimeout(() -> redisMessageListenerContainer.removeMessageListener(messageListener));
    }

    private String getChannelName(final String makersId) {
        return "topics:" + makersId;
    }
}
