package co.kurrant.app.public_api.service;

import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.LoginRequestDto;
import co.kurrant.app.public_api.dto.user.LoginResponseDto;
import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {
    // 이메일 인증
    void mailConfirm(MailMessageDto mailMessageDto) throws Exception;
    // Sms 인증
    void sendSms(SmsMessageRequestDto smsMessageRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 회원가입
    User signUp(SignUpRequestDto signUpRequestDto);
    // 로그인
    LoginResponseDto login(LoginRequestDto loginRequestDto);

}
