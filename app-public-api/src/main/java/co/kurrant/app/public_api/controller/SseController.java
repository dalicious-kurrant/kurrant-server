package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.sse.NotificationReqDto;
import co.dalicious.client.sse.SseService;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.Authentication;

import java.math.BigInteger;


@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final UserUtil userUtil;

    @GetMapping(value = "/v1/notification/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(Authentication authentication,
                                     @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        System.out.println("authentication = " + authentication);
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        return sseService.subscribe(userId, lastEventId);
    }

    @PostMapping("/v1/notification/read")
    public ResponseMessage readNotification(Authentication authentication, @RequestBody NotificationReqDto notificationDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        return ResponseMessage.builder()
                .data(sseService.readNotification(userId, notificationDto))
                .message("알림을 읽으셨습니다.").build();
    }
}
