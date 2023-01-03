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
    // 그룹(기업)에 등록되어 있는 유저인지 확인
    void
    // 스팟이 존재하는 유저인지 확인
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
