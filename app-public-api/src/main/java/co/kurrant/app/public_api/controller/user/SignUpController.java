package co.kurrant.app.public_api.controller.user;

import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
import co.kurrant.app.public_api.service.SignUpService;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.SmsMessageDto;
import co.dalicious.client.external.sms.SmsResponseDto;
import co.dalicious.client.external.sms.SmsService;
import co.dalicious.system.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignUpController {
    private final EmailService emailService;
    private final SmsService smsService;
    private final SignUpService signUpService;


    @PostMapping("/v1/auth/certification/email")
    public String mailConfirm(@RequestBody MailMessageDto mailMessageDto) throws Exception {
        return emailService.sendSimpleMessage(mailMessageDto.getReceivers());
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
    public SmsResponseDto smsConfirm(@RequestBody SmsMessageDto smsMessageDto) throws Exception {
        return smsService.sendSms(smsMessageDto);
    }

    @GetMapping("/v1/auth/certification/phone")
    public ResponseMessage checkSmsCertificationNumber(@RequestParam("key")String key) throws Exception {
        smsService.verifySms(key);
        return ResponseMessage.builder()
                .result(true)
                .message("휴대폰 인증에 성공하였습니다.")
                .build();
    }

    @PostMapping("/v1/auth/join")
    public ResponseMessage signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseMessage.builder()
                .result(true)
                .message("회원가입에 성공하셨습니다.")
                .data(signUpService.SignUp(signUpRequestDto))
                .build();
    }
}
