package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.dto.push.PushByTopicRequestDto;
import co.kurrant.app.admin_api.dto.push.PushRequestDto;
import co.kurrant.app.admin_api.service.PushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "A. Push ")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/push")
@RestController
public class PushController {

    private final PushService pushService;

    @PostMapping("")
    @Operation(summary = "토큰으로 푸쉬 알림 보내기", description = "FCM 토큰으로 푸쉬 알림을 보낸다.")
    public ResponseMessage sendToPush(PushRequestDto pushRequestDto) {
        pushService.sendToPush(pushRequestDto);
        return ResponseMessage.builder()
                .message("메시지를 발송했습니다.")
                .build();
    }

    @PostMapping("/topics")
    @Operation(summary = "주제별로 push 보내기", description = "주제별로 푸쉬 알림을 보낸다.")
    public ResponseMessage sendByTopic(PushByTopicRequestDto pushByTopicRequestDto){
        pushService.sendByTopic(pushByTopicRequestDto);
        return ResponseMessage.builder()
                .message("주제별 메시지보내기에 성공하였습니다.")
                .build();
    }


}
