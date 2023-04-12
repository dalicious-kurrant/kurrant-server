//package co.dalicious.client.alarm;
//
//import co.dalicious.client.alarm.dto.AlimtalkRequestDto;
//import co.dalicious.client.alarm.dto.PushByTopicRequestDto;
//import co.dalicious.client.alarm.dto.PushRequestDto;
//import co.dalicious.client.alarm.dto.PushTokenSaveReqDto;
//import co.dalicious.client.alarm.service.PushService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.json.simple.parser.ParseException;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@Tag(name = "A. Push ")
//@RequiredArgsConstructor
//@RequestMapping(value = "/v1/push")
//@RestController
//public class PushController {
//
//    private final PushService pushService;
//
//    @PostMapping("")
//    @Operation(summary = "토큰으로 푸쉬 알림 보내기", description = "FCM 토큰으로 푸쉬 알림을 보낸다.")
//    public ResponseMessage sendToPush(@RequestBody PushRequestDto pushRequestDto) {
//        pushService.sendToPush(pushRequestDto);
//        return ResponseMessage.builder()
//                .message("메시지를 발송했습니다.")
//                .build();
//    }
//
//    @PostMapping("/topics")
//    @Operation(summary = "주제별로 push 보내기", description = "주제별로 푸쉬 알림을 보낸다.")
//    public ResponseMessage sendByTopic(@RequestBody PushByTopicRequestDto pushByTopicRequestDto){
//        pushService.sendByTopic(pushByTopicRequestDto);
//        return ResponseMessage.builder()
//                .message("주제별 메시지보내기에 성공하였습니다.")
//                .build();
//    }
//
//    @PostMapping("/talk")
//    @Operation(summary = "알림톡 보내기 샘플", description = "알림톡을 보낸다")
//    public ResponseMessage sendAlimTalk(@RequestBody AlimtalkRequestDto alimtalkRequestDto) throws IOException, ParseException {
//        pushService.sendToTalk(alimtalkRequestDto);
//        return ResponseMessage.builder()
//                .message("알림톡 발송 성공!")
//                .build();
//    }
//
//    @PostMapping("/save/token")
//    @Operation(summary = "FCM 토큰 저장하기", description = "유저정보에 FCM토큰을 저장한다")
//    public ResponseMessage tokenSave(@RequestBody PushTokenSaveReqDto pushTokenSaveReqDto){
//        pushService.saveToken(pushTokenSaveReqDto);
//        return ResponseMessage.builder()
//                .message("토큰 저장 성공!")
//                .build();
//    }
//
//
//
//}