package co.dalicious.data.redis.pubsub;

import co.dalicious.system.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final SseEventService sseEventService;

    public void publishEvent(String message) {
        sseEventService.send(StringUtils.parseBigIntegerList(message));
    }
}
