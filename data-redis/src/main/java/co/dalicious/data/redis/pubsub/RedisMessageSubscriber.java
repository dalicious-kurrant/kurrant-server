package co.dalicious.data.redis.pubsub;

import co.dalicious.system.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final SseEventService sseEventService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String receivedMessage = new String(message.getBody());
        List<BigInteger> makersIds = StringUtils.parseBigIntegerList(receivedMessage);
        sseEventService.send(makersIds);
    }
}
