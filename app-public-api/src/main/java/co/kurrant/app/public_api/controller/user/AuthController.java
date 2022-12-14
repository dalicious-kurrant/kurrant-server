package co.kurrant.app.public_api.controller.user;

import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.system.util.RequiredAuth;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.service.AuthService;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.util.VerifyUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Tag(name = "1. Auth")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/auth")
@RestController
public class AuthController {
    private final EmailService emailService;
    private final VerifyUtil verifyUtil;
    private final AuthService authService;

    @Operation(summary = "이메일 인증번호 발송", description = "이메일 인증번호를 발송한다.")
    @PostMapping("/certification/email")
    public ResponseMessage mailConfirm(@RequestBody MailMessageDto mailMessageDto, @RequestParam("type")String type) throws Exception {
        authService.mailConfirm(mailMessageDto, type);
        return ResponseMessage.builder()
                .message("인증번호가 발송되었습니다.")
                .build();
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증번호를 검증한다.")
    @GetMapping("/certification/email")
    public ResponseMessage checkEmailCertificationNumber(@RequestParam("key")String key, @RequestParam("type")String type) {
        verifyUtil.verifyCertificationNumber(key, RequiredAuth.ofId(type));
        return ResponseMessage.builder()
                .message("이메일 인증에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 인증번호 발송", description = "휴대폰 인증번호를 발송한다.")
    @PostMapping("/certification/phone")
    public ResponseMessage smsConfirm(@RequestBody SmsMessageRequestDto smsMessageRequestDto, @RequestParam("type")String type) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        authService.sendSms(smsMessageRequestDto, type);
        return ResponseMessage.builder()
                .message("인증번호가 발송되었습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 인증", description = "휴대폰 인증번호를 검증한다.")
    @GetMapping("/certification/phone")
    public ResponseMessage checkSmsCertificationNumber(@RequestParam("key")String key, @RequestParam("type")String type) {
        verifyUtil.verifyCertificationNumber(key, RequiredAuth.ofId(type));
        return ResponseMessage.builder()
                .message("휴대폰 인증에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "아이디 찾기", description = "유저의 아이디를 찾는다.")
    @GetMapping("/inquiry/id")
    public ResponseMessage findUserEmail(@RequestBody FindIdRequestDto findIdRequestDto) {
        return ResponseMessage.builder()
                .message("아이디 찾기에 성공하였습니다.")
                .data(authService.findUserEmail(findIdRequestDto))
                .build();
    }

    @Operation(summary = "비밀번호 찾기시 회원정보 확인", description = "비밀번호 변경을 요청하는 유저가 유효한지 검사한다.")
    @PostMapping("/inquiry/password")
    public ResponseMessage findUserPasswordUserCheck(@RequestBody FindPasswordUserCheckRequestDto findPasswordUserCheckRequestDto) {
        authService.checkUser(findPasswordUserCheckRequestDto);
        return ResponseMessage.builder()
                .message("회원정보가 존재합니다.")
                .build();
    }

    @Operation(summary = "비밀번호 찾기 이메일로 비밀번호 재설정", description = "비밀번호를 재설정한다.")
    @PostMapping("/inquiry/password/email")
    public ResponseMessage findUserPasswordEmail(@RequestBody FindPasswordEmailRequestDto findPasswordEmailRequestDto) {
        authService.findPasswordEmail(findPasswordEmailRequestDto);
        return ResponseMessage.builder()
                .message("비밀번호 찾기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "비밀번호 찾기 휴대폰으로 비밀번호 재설정", description = "비밀번호를 재설정한다.")
    @PostMapping("/inquiry/password/phone")
    public ResponseMessage findUserPasswordPhone(@RequestBody FindPasswordPhoneRequestDto findPasswordPhoneRequestDto) {
        authService.findPasswordPhone(findPasswordPhoneRequestDto);
        return ResponseMessage.builder()
                .message("비밀번호 찾기에 성공하였습니다.")
                .build();
    }



    @Operation(summary = "회원가입", description = "회원가입을 수행한다.")
    @PostMapping("/join")
    public ResponseMessage signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseMessage.builder()
                .message("회원가입에 성공하셨습니다.")
                .data(authService.signUp(signUpRequestDto))
                .build();
    }

    @Operation(summary = "로그인", description = "로그인을 수행한다.")
    @PostMapping("/login")
    public ResponseMessage login(@Parameter(name = "로그인정보", description = "",
            required = true) @Valid @RequestBody LoginRequestDto dto) {
        return ResponseMessage.builder()
                .message("로그인에 성공하였습니다.")
                .data(authService.login(dto))
                .build();
    }
}
