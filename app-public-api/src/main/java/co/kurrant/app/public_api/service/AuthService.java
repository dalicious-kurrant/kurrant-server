package co.kurrant.app.public_api.service;

import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {
    // 이메일 인증
    void mailConfirm(MailMessageDto mailMessageDto, String type) throws Exception;
    // Sms 인증
    void sendSms(SmsMessageRequestDto smsMessageRequestDto, String type) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 회원가입
    User signUp(SignUpRequestDto signUpRequestDto);
    // 로그인
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    // SNS 로그인 / 회원가입
    LoginResponseDto snsLoginOrJoin(String sns, SnsAccessToken snsAccessToken);
    // 아이디 찾기
    FindIdResponseDto findUserEmail(FindIdRequestDto findIdRequestDto);
    // 비밀번호 찾기시 회원 정보 확인
    void checkUser(FindPasswordUserCheckRequestDto findPasswordUserCheckRequestDto);
    // 비밀번호 찾기시 이메일 인증을 통해 비밀번호 변경
    void findPasswordEmail(FindPasswordEmailRequestDto findPasswordEmailRequestDto);
    // 비밀번호 찾기시 휴대폰 인증을 통해 비밀번호 변경
    void findPasswordPhone(FindPasswordPhoneRequestDto findPasswordPhoneRequestDto);
}
