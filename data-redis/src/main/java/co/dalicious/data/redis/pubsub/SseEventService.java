package co.dalicious.data.redis.pubsub;

import co.dalicious.data.redis.repository.EmitterRepository;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
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
    private final RedisOperations<String, String> eventRedisOperations;
    private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public SseEmitter subscribe(BigInteger makersId, String lastEventId) {
        //구독한 유저를 특정하기 위한 id.
        String id = String.valueOf(makersId);

        //생성한 emitter를 저장한다. emitter는 HTTP/2기준 브라우저 당 100개 만들 수 있다.
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        System.out.println("emitter = " + emitter);

        final MessageListener messageListener = (message, pattern) -> {
            sendToClient(emitter, id, "EventStream Created. [makersId=" + makersId + "]");
        };

        redisMessageListenerContainer.addMessageListener(messageListener, ChannelTopic.of(getChannelName(id)));

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
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
            emitters.remove(emitter);
            throw new ApiException(ExceptionEnum.CONNECTION_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void send(Collection<BigInteger> receivers) {
        String notification = "Reload! " + "(" + DateUtils.localDateTimeToString(LocalDateTime.now()) + ")";

        for (BigInteger receiver : receivers) {
            String id = String.valueOf(receiver);
            eventRedisOperations.convertAndSend(getChannelName(id), notification);
        }
    }

    private void checkEmitterStatus(final SseEmitter emitter, final MessageListener messageListener) {
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            redisMessageListenerContainer.removeMessageListener(messageListener);
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            redisMessageListenerContainer.removeMessageListener(messageListener);
        });
    }

    private String getChannelName(final String makersId) {
        return "topics:" + makersId;
    }
}
