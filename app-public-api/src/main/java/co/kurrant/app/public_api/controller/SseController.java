package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.data.redis.event.ReloadEvent;
import co.dalicious.data.redis.pubsub.SseEventService;
import co.dalicious.data.redis.pubsub.SseService;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.public_api.dto.SseTypeDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@Tag(name = "SSE")
@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserUtil userUtil;

    @Description(value = "sse 구독")
    @GetMapping(value = "/v1/notification/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(Authentication authentication,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        return sseService.subscribe(userId, lastEventId);
    }

    @Description(value = "메세지 전송")
    @PostMapping(value = "/v1/notification/send")
    public ResponseMessage subscribe(@RequestBody OrderDto.IdList idList) {
        applicationEventPublisher.publishEvent(new ReloadEvent(idList.getIdList()));
        return ResponseMessage.builder()
                .message("메세지 전송 성공")
                .build();
    }

    @Description(value = "sse 알림 조회")
    @GetMapping("/v1/notification")
    public ResponseMessage getAllNotification(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("알림 목록 조회에 성공했습니다.")
                .data(sseService.getAllNotification(securityUser.getId()))
                .build();
    }

    @Description(value = "sse 알림 읽기(읽은 알림 삭제)")
    @PutMapping(value = "/v1/notification/read", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage readNotification(Authentication authentication, @RequestBody SseTypeDto dto) {
        BigInteger userId = userUtil.getUserId(UserUtil.securityUser(authentication));
        sseService.readNotification(userId, dto.getType(), dto.getIds());
        return ResponseMessage.builder()
                .message("알림 읽기에 성공했습니다.")
                .build();
    }
}
