package co.kurrant.app.makers_api.controller;

import co.dalicious.data.redis.pubsub.SseEventService;
import co.kurrant.app.makers_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/sse")
public class SseController {
    private final SseEventService sseEventService;
    @Description(value = "sse 구독")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(Authentication authentication,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        return sseEventService.subscribe(UserUtil.getMakersId(authentication), lastEventId);
    }
}
