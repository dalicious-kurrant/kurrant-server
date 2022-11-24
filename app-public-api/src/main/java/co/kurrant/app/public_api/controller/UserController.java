package co.kurrant.app.public_api.controller;

import co.dalicious.data.redis.mail.EmailService;
import co.dalicious.data.redis.sms.MessageDto;
import co.dalicious.data.redis.sms.SmsResponseDto;
import co.dalicious.data.redis.sms.SmsService;
import co.dalicious.system.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final EmailService emailService;
    private final SmsService smsService;


    @PostMapping("/v1/auth/certification/email")
    public String mailConfirm(@RequestBody MessageDto messageDto) throws Exception {
        return emailService.sendSimpleMessage(messageDto.getTo());
    }

    @GetMapping("/v1/auth/certification/email")
    public ResponseMessage checkEmailCertificationNumber(@RequestParam("key")String key) throws Exception {
        emailService.verifyEmail(key);
        return ResponseMessage.builder()
                .result(true)
                .message("이메일 인증에 성공하였습니다.")
                .build();
    }

    @PostMapping("/v1/auth/certification/phone")
    public SmsResponseDto smsConfirm(@RequestBody MessageDto messageDto) throws Exception {
        return smsService.sendSms(messageDto);
    }

    @GetMapping("/v1/auth/certification/phone")
    public ResponseMessage checkSmsCertificationNumber(@RequestParam("key")String key) throws Exception {
        smsService.verifySms(key);
        return ResponseMessage.builder()
                .result(true)
                .message("휴대폰 인증에 성공하였습니다.")
                .build();
    }
}
