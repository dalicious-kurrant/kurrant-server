package co.dalicious.data.redis.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic redisTopic;
    public void publishEvent(String message) {
        redisTemplate.convertAndSend(redisTopic.getTopic(), message);
    }
}
