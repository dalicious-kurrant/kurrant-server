package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.sse.NotificationReqDto;
import co.dalicious.client.sse.SseService;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.Authentication;

import java.math.BigInteger;

@Tag(name="SSE")
@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final UserUtil userUtil;

    @Description(value = "sse 구독")
    @GetMapping(value = "/v1/notification/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(Authentication authentication,
                                     @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        return sseService.subscribe(userId, lastEventId);
    }

    @Description(value = "sse 알림 조회")
    @GetMapping("/v1/notification")
    public ResponseMessage getAllNotification(Authentication authentication, @RequestParam Integer type) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        return ResponseMessage.builder()
                .message("알림 목록 조회에 성공했습니다.")
                .data(sseService.getAllNotification(userId, type))
                .build();
    }

    @Description(value = "sse 알림 읽기(읽은 알림 삭제)")
    @PutMapping("/v1/notification/read")
    public void readNotification(Authentication authentication, @RequestBody Integer type) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        sseService.readNotification(userId, type);
    }
}
