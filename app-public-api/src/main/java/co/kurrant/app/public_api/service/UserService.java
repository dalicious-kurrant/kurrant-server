package co.kurrant.app.public_api.service;

import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.model.SecurityUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {
    // 마이페이지(홈) 유저 정보 가져오기
    UserInfoDto getUserInfo(User user);
    // 홈 유저 정보 가져오기
    UserHomeResponseDto getUserHomeInfo(User user);
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
    UserPersonalInfoDto getPersonalUserInfo(SecurityUser securityUser);
    // TODO: 추후 백오피스 구현시 삭제
    void settingGroup(HttpServletRequest httpServletRequest, BigInteger groupId);
    // 멤버십 구독 정보를 가져온다.
    List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo();
    // 유저가 속한 그룹 정보 리스트
    List<SpotListResponseDto> getClients(SecurityUser securityUser);
}
