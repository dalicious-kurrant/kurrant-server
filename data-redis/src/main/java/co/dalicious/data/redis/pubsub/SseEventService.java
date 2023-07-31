package co.dalicious.data.redis.pubsub;

import co.dalicious.data.redis.repository.EmitterRepository;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SseEventService {
    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(BigInteger makersId, String lastEventId) {
        //구독한 유저를 특정하기 위한 id.
        String id = String.valueOf(makersId);

        //생성한 emitter를 저장한다. emitter는 HTTP/2기준 브라우저 당 100개 만들 수 있다.
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        System.out.println("emitter = " + emitter);

        //기존 emitter 중 완료 되거나 시간이 초과되어 연결이 끊긴 emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        emitterRepository.save(id, emitter);

        // 503 에러를 방지하기 위한 더미 이벤트 전송. 연결 중 한 번도 이벤트를 보낸 적이 없다면 다음 연결 때 503에러를 낸다.
        sendToClient(emitter, id, "EventStream Created. [makersId=" + makersId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(makersId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

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
            throw new ApiException(ExceptionEnum.CONNECTION_ERROR);
        }
    }

    @Transactional
    public void send(Collection<BigInteger> receivers) {
        String notification = "Reload! " + "(" + DateUtils.localDateTimeToString(LocalDateTime.now()) + ")";

        for (BigInteger receiver : receivers) {
            String id = String.valueOf(receiver);

            // Fetch SseEmitter for each receiver
            Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
            if (sseEmitters.isEmpty()) continue;
            sseEmitters.forEach(
                    (key, emitter) -> {
                        // Store the data in cache to handle any lost data
                        emitterRepository.saveEventCache(key, notification);
                        // Send the data
                        sendToClient(emitter, key, notification);
                    }
            );
        }
    }
}
