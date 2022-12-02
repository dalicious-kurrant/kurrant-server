package co.kurrant.app.public_api.service;

import co.dalicious.client.external.sms.SmsMessageDto;
import co.dalicious.domain.user.dto.UserDto;
import co.kurrant.app.public_api.dto.user.ChangePasswordRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public interface UserService {
    // 휴대폰 번호 변경
    void changePhoneNumber(HttpServletRequest httpServletRequest, SmsMessageDto smsMessageDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 비밀번호 변경
    void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto);
    // 유저 정보 가져오기
    UserDto getUserInfo(HttpServletRequest httpServletRequest);

}
