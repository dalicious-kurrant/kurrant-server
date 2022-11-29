package co.kurrant.app.public_api.controller.user;

import co.kurrant.app.public_api.dto.user.LoginRequestDto;
import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
import co.kurrant.app.public_api.service.AuthService;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.SmsMessageDto;
import co.dalicious.client.external.sms.SmsService;
import co.dalicious.system.util.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "1. Auth")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@RestController
public class AuthController {
    private final EmailService emailService;
    private final SmsService smsService;
    private final AuthService authService;

    @Operation(summary = "이메일 인증번호 발송", description = "이메일 인증번호를 발송한다.")
    @PostMapping("/certification/email")
    public ResponseMessage mailConfirm(@RequestBody MailMessageDto mailMessageDto) throws Exception {
        authService.mailConfirm(mailMessageDto);
        return ResponseMessage.builder()
                .result(true)
                .message("인증번호가 발송되었습니다.")
                .build();
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증번호를 검증한다.")
    @GetMapping("/certification/email")
    public ResponseMessage checkEmailCertificationNumber(@RequestParam("key")String key) throws Exception {
        emailService.verifyEmail(key);
        return ResponseMessage.builder()
                .result(true)
                .message("이메일 인증에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 인증번호 발송", description = "휴대폰 인증번호를 발송한다.")
    @PostMapping("/certification/phone")
    public ResponseMessage smsConfirm(@RequestBody SmsMessageDto smsMessageDto) throws Exception {
        authService.sendSms(smsMessageDto);
        return ResponseMessage.builder()
                .result(true)
                .message("인증번호가 발송되었습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 인증", description = "휴대폰 인증번호를 검증한다.")
    @GetMapping("/certification/phone")
    public ResponseMessage checkSmsCertificationNumber(@RequestParam("key")String key) throws Exception {
        smsService.verifySms(key);
        return ResponseMessage.builder()
                .result(true)
                .message("휴대폰 인증에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "회원가입", description = "회원가입을 수행한다.")
    @PostMapping("/join")
    public ResponseMessage signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseMessage.builder()
                .result(true)
                .message("회원가입에 성공하셨습니다.")
                .data(authService.signUp(signUpRequestDto))
                .build();
    }

    @Operation(summary = "로그인", description = "로그인을 수행한다.")
    @PostMapping("/login")
    public ResponseMessage login(@Parameter(name = "로그인정보", description = "",
            required = true) @Valid @RequestBody LoginRequestDto dto) {
        return ResponseMessage.builder()
                .result(true)
                .message("로그인에 성공하셨습니다.")
                .data(authService.login(dto))
                .build();
    }
}
