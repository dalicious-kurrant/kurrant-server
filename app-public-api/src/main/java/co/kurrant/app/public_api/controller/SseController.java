package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.sse.NotificationDto;
import co.dalicious.client.sse.SseService;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.Authentication;

import java.math.BigInteger;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/notification")
public class SseController {

    private final SseService sseService;
    private final UserUtil userUtil;
    private final UserRepository userRepository;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public ResponseMessage subscribe(Authentication authentication,
                                     @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        System.out.println("authentication = " + authentication);
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger userId = userUtil.getUserId(securityUser);
        return ResponseMessage.builder()
                .data(sseService.subscribe(userId, lastEventId))
                .message("구독에 성공하였습니다.")
                .build();
    }

    @PostMapping("/read")
    public ResponseMessage readNotification(Authentication authentication, @RequestBody NotificationDto notificationDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        User user = userUtil.getUser(securityUser);
        return ResponseMessage.builder()
                .data(sseService.readNotification(user, notificationDto))
                .message("알림을 읽으셨습니다.").build();
    }
}
