package co.kurrant.app.admin_api.controller;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.HandlePushAlarmDto;
import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.kurrant.app.admin_api.dto.alimtalk.AlimtalkTestDto;
import co.kurrant.app.admin_api.service.PushAlarmService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/alarms/push")
public class PushAlarmController {

    private final PushAlarmService pushAlarmService;

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "자동 푸시 알림 조건 조회", description = "자동 푸시 알림 조건을 조회한다.")
    @GetMapping("/auto")
    public ResponseMessage findAllAutoPushAlarmList() {
        return ResponseMessage.builder()
                .message("자동 푸시 알림 조건을 조회했습니다.")
                .data(pushAlarmService.findAllAutoPushAlarmList())
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "자동 푸시 알림 메시지 수정", description = "자동 푸시 알림 메시지를 수정한다.")
    @PatchMapping("/auto/message")
    public ResponseMessage updateAutoPushAlarmMessage(@RequestBody AutoPushAlarmDto.AutoPushAlarmMessageReqDto reqDto) {
        pushAlarmService.updateAutoPushAlarmMessage(reqDto);
        return ResponseMessage.builder()
                .message("자동 푸시 알림 메시지를 수정했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "자동 푸시 알림 상태 수정", description = "자동 푸시 알림 상태를 수정한다.")
    @PatchMapping("/auto/status")
    public ResponseMessage updateAutoPushAlarmStatus(@RequestBody AutoPushAlarmDto.AutoPushAlarmStatusReqDto reqDto) {
        pushAlarmService.updateAutoPushAlarmStatus(reqDto);
        return ResponseMessage.builder()
                .message("자동 푸시 알림 상태를 수정했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "자동 푸시 알림 url 수정", description = "자동 푸시 알림 url을 수정한다.")
    @PatchMapping("/auto/url")
    public ResponseMessage updateAutoPushAlarmUrl(@RequestBody AutoPushAlarmDto.AutoPushAlarmUrlReqDto reqDto) {
        pushAlarmService.updateAutoPushAlarmUrl(reqDto);
        return ResponseMessage.builder()
                .message("자동 푸시 알림 url을 수정했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "수동 푸시 알림 타입 조회", description = "수동 푸시 알림 타입을 조회한다.")
    @GetMapping("/handle/type")
    public ResponseMessage findAllTypeList() {
        return ResponseMessage.builder()
                .message("수동 푸시 알림 타입 조건을 조회했습니다.")
                .data(pushAlarmService.findAllTypeList())
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "수동 푸시 알림 그룹 조회", description = "수동 푸시 알림 그룹을 조회한다.")
    @GetMapping("/handle")
    public ResponseMessage findAllListByType(@RequestParam Integer type) {
        return ResponseMessage.builder()
                .message("수동 푸시 알림 조건에 맞는 데이터를 조회했습니다.")
                .data(pushAlarmService.findAllListByType(type))
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "수동 푸시 알림", description = "수동 푸시 알림을 생성합니다.")
    @PostMapping("/handle")
    public ResponseMessage createHandlePushAlarmList(@RequestBody List<HandlePushAlarmDto.HandlePushAlarmReqDto> reqDtoList) {
        pushAlarmService.createHandlePushAlarmList(reqDtoList);
        return ResponseMessage.builder()
                .message("수동 푸시 알림을 생성했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.PUSH_ALARM)
    @Operation(summary = "카카오 알림톡 테스트", description = "카카오 알림톡을 발송한다")
    @PostMapping("/alimtalk")
    public ResponseMessage kakaoAlimtalkTest(@RequestBody AlimtalkTestDto alimtalkTestDto) throws IOException, ParseException {
        pushAlarmService.alimtalkSendTest(alimtalkTestDto);
        return ResponseMessage.builder()
                .message("알림톡 전송 성공")
                .build();
    }

}
