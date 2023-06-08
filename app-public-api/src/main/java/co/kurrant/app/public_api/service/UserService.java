package co.kurrant.app.public_api.service;

import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.payment.dto.BillingKeyDto;
import co.dalicious.domain.payment.dto.CreditCardDefaultSettingDto;
import co.dalicious.domain.payment.dto.CreditCardResponseDto;
import co.dalicious.domain.payment.dto.DeleteCreditCardDto;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.dto.SaveDailyReportFoodReqDto;
import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.SaveDailyReportDto;
import co.kurrant.app.public_api.dto.board.AlarmResponseDto;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.model.SecurityUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface UserService {
    // 마이페이지(홈) 유저 정보 가져오기
    UserInfoDto getUserInfo(SecurityUser securityUser);
    // 홈 유저 정보 가져오기
    UserHomeResponseDto getUserHomeInfo(SecurityUser securityUser);
    // SNS 계정 연결
    void connectSnsAccount(SecurityUser securityUser, SnsAccessToken snsAccessToken, String sns);
    void connectAppleAccount(SecurityUser securityUser, Map<String,Object> appleLoginDto) throws JsonProcessingException;
    // SNS 계정 해제
    void disconnectSnsAccount(SecurityUser securityUser, String sns);
    // 휴대폰 번호 변경
    void changePhoneNumber(SecurityUser securityUser, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 비밀번호 변경
    void changePassword(SecurityUser securityUser, ChangePasswordDto changePasswordRequestDto);
    // 이메일/비밀번호 설정
    void setEmailAndPassword(SecurityUser securityUser, SetEmailAndPasswordDto setEmailAndPasswordDto);
    List<MarketingAlarmResponseDto> getAlarmSetting(SecurityUser securityUser);
    List<MarketingAlarmResponseDto> changeAlarmSetting(SecurityUser securityUser, MarketingAlarmRequestDto marketingAlarmDto);
    // 마이페이지(개인정보) 유저 정보 가져오기
    UserPersonalInfoDto getPersonalUserInfo(SecurityUser securityUser);
    // 오픈스팟 그룹 설정
    void settingOpenGroup(SecurityUser securityUser, BigInteger groupId);
    // 유저가 속한 그룹 정보 리스트
    GroupCountDto getClients(SecurityUser securityUser);
    Integer isHideEmail(SecurityUser securityUser);
    Object createNiceBillingKeyFirst(SecurityUser securityUser, Integer typeId, BillingKeyDto billingKeyDto) throws IOException, ParseException;
    List<CreditCardResponseDto> getCardList(SecurityUser securityUser);
    void patchDefaultCard(SecurityUser securityUser, CreditCardDefaultSettingDto creditCardDefaultSettingDto);
    void deleteCard(DeleteCreditCardDto deleteCreditCardDto);
    void changeName(SecurityUser securityUser, ChangeNameDto changeNameDto);
    void withdrawal(SecurityUser securityUser);
    void withdrawalCancel(SecurityUser securityUser);
    LoginResponseDto autoLogin(HttpServletRequest httpServletRequest);

    void saveToken(FcmTokenSaveReqDto fcmTokenSaveReqDto, SecurityUser securityUser);

    String savePaymentPassword(SecurityUser securityUser, SavePaymentPasswordDto savePaymentPasswordDto);

    String checkPaymentPassword(SecurityUser securityUser, SavePaymentPasswordDto savePaymentPasswordDto);

    Boolean isPaymentPassword(SecurityUser securityUser);

    void paymentPasswordReset(SecurityUser securityUser, PaymentResetReqDto resetDto);

    String userPreferenceSave(SecurityUser securityUser, UserPreferenceDto userPreferenceDto);

    Object getCountry();

    Object getFavoriteCountryFoods(Integer code);

    Object getJobType(Integer category, String code);

    Object getFoodImage(List<BigInteger> foodId);

    Object getTestData();

    Boolean userPreferenceCheck(SecurityUser securityUser);

    List<PushResponseDto> getAlarms(SecurityUser securityUser);
    void insertMyFood(SecurityUser securityUser, SaveDailyReportDto saveDailyReportDto);
    Object getReport(SecurityUser securityUser, String date);
    void saveDailyReportFood(SaveDailyReportFoodReqDto dto);
    String deleteReport(SecurityUser securityUser, BigInteger reportId);
    Object getOrderByDateAndDiningType(SecurityUser securityUser, String date, Integer diningType);
    void allChangeAlarmSetting(SecurityUser securityUser, Boolean isActive);

}
