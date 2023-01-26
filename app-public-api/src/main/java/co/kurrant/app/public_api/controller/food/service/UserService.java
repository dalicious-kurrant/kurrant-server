package co.kurrant.app.public_api.controller.food.service;

import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.payment.dto.CreditCardDefaultSettingDto;
import co.dalicious.domain.payment.dto.CreditCardResponseDto;
import co.dalicious.domain.payment.dto.DeleteCreditCardDto;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.model.SecurityUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {
    // 마이페이지(홈) 유저 정보 가져오기
    UserInfoDto getUserInfo(SecurityUser securityUser);
    // 홈 유저 정보 가져오기
    UserHomeResponseDto getUserHomeInfo(SecurityUser securityUser);
    // SNS 계정 연결
    void connectSnsAccount(SecurityUser securityUser, SnsAccessToken snsAccessToken, String sns);
    // SNS 계정 해제
    void disconnectSnsAccount(SecurityUser securityUser, String sns);
    // 휴대폰 번호 변경
    void changePhoneNumber(SecurityUser securityUser, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 비밀번호 변경
    void changePassword(SecurityUser securityUser, ChangePasswordDto changePasswordRequestDto);
    // 이메일/비밀번호 설정
    void setEmailAndPassword(SecurityUser securityUser, SetEmailAndPasswordDto setEmailAndPasswordDto);
    // 알람/마케팅 설정 조회
    MarketingAlarmResponseDto getAlarmSetting(SecurityUser securityUser);
    // 알람/마케팅 설정 변경
    MarketingAlarmResponseDto changeAlarmSetting(SecurityUser securityUser, MarketingAlarmRequestDto marketingAlarmDto);
    // 마이페이지(개인정보) 유저 정보 가져오기
    UserPersonalInfoDto getPersonalUserInfo(SecurityUser securityUser);
    // TODO: 추후 백오피스 구현시 삭제
    void settingGroup(SecurityUser securityUser, BigInteger groupId);
    // 멤버십 구독 정보를 가져온다.
    List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo();
    // 유저가 속한 그룹 정보 리스트
    List<SpotListResponseDto> getClients(SecurityUser securityUser);

    Integer saveCreditCard(SecurityUser securityUser, SaveCreditCardRequestDto saveCreditCardRequestDto);

    List<CreditCardResponseDto> getCardList(SecurityUser securityUser);

    void patchDefaultCard(SecurityUser securityUser, CreditCardDefaultSettingDto creditCardDefaultSettingDto);

    void deleteCard(DeleteCreditCardDto deleteCreditCardDto);
}
