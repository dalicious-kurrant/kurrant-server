package co.kurrant.app.public_api.service;

import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.kurrant.app.public_api.dto.user.ChangePasswordRequestDto;
import co.kurrant.app.public_api.dto.user.ChangePhoneRequestDto;
import co.kurrant.app.public_api.dto.user.SetEmailAndPasswordDto;
import co.kurrant.app.public_api.dto.user.UserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.aspectj.apache.bcel.classfile.Module;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public interface UserService {
    // SNS 계정 연결 및 해제
    void editSnsAccount(HttpServletRequest httpServletRequest, String sns);
    // SNS 계정 연결
    void connectSnsAccount(UserInfoDto userInfoDto, Provider provider);
    // SNS 계정 해제
    void disconnectSnsAccount(ProviderEmail providerEmail, Provider provider);
    // 휴대폰 번호 변경
    void changePhoneNumber(HttpServletRequest httpServletRequest, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 비밀번호 변경
    void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto);
    // 이메일/비밀번호 설정
    void setEmailAndPassword(HttpServletRequest httpServletRequest, SetEmailAndPasswordDto setEmailAndPasswordDto);
    // 유저 정보 가져오기
    UserInfoDto getUserInfo(HttpServletRequest httpServletRequest);

}
