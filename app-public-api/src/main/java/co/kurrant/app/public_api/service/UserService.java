package co.kurrant.app.public_api.service;

import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public interface UserService {
    // 홈 유저 정보 가져오기
    UserHomeResponseDto getUserHomeInfo(HttpServletRequest httpServletRequest);
    // SNS 계정 연결
    void connectSnsAccount(HttpServletRequest httpServletRequest, SnsAccessToken snsAccessToken, String sns);
    // SNS 계정 해제
    void disconnectSnsAccount(HttpServletRequest httpServletRequest, String sns);
    // 휴대폰 번호 변경
    void changePhoneNumber(HttpServletRequest httpServletRequest, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 비밀번호 변경
    void changePassword(HttpServletRequest httpServletRequest, ChangePasswordDto changePasswordRequestDto);
    // 이메일/비밀번호 설정
    void setEmailAndPassword(HttpServletRequest httpServletRequest, SetEmailAndPasswordDto setEmailAndPasswordDto);
    // 알람/마케팅 설정 조회
    MarketingAlarmResponseDto getAlarmSetting(HttpServletRequest httpServletRequest);
    // 알람/마케팅 설정 변경
    MarketingAlarmResponseDto changeAlarmSetting(HttpServletRequest httpServletRequest, MarketingAlarmRequestDto marketingAlarmDto);
    // 마이페이지(개인정보) 유저 정보 가져오기
    UserPersonalInfoDto getPersonalUserInfo(HttpServletRequest httpServletRequest);
    // 마이페이지(홈) 유저 정보 가져오기
    UserInfoDto getUserInfo(HttpServletRequest httpServletRequest);
    // TODO: 추후 백오피스 구현시 삭제
    void settingCorporation(HttpServletRequest httpServletRequest, Integer corporationId);
    // TODO: 추후 백오피스 구현시 삭제
    void settingApartment(HttpServletRequest httpServletRequest, Integer apartmentId);
}
