package co.kurrant.app.public_api.controller.user;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.payment.dto.BillingKeyDto;
import co.dalicious.domain.payment.dto.CreditCardDefaultSettingDto;
import co.dalicious.domain.payment.dto.DeleteCreditCardDto;
import co.dalicious.domain.user.dto.UserPreferenceDto;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "2. User")
@RequestMapping(value = "/v1/users/me")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "마이페이지 유저 가져오기", description = "로그인 한 유저 정보를 불러온다.")
    @GetMapping("")
    public UserInfoDto getUserInfo(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return userService.getUserInfo(securityUser);
    }

    @Operation(summary = "자동 로그인", description = "자동 로그인을 한다.")
    @GetMapping("/autoLogin")
    public LoginResponseDto autoLogin(HttpServletRequest httpServletRequest) {
        return userService.autoLogin(httpServletRequest);
    }

    @Operation(summary = "마이페이지의 개인 정보 페이지에서 유저 정보 가져오기", description = "개인 정보 페이지에서 로그인 한 유저의 개인 정보를 불러온다.")
    @GetMapping("/personal")
    public UserPersonalInfoDto getPersonalUserInfo(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return userService.getPersonalUserInfo(securityUser);
    }

    @Operation(summary = "홈 유저 정보 가져오기", description = "로그인 한 유저의 정보를 불러온다.")
    @GetMapping("/userInfo")
    public UserHomeResponseDto userHomeInfo(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return userService.getUserHomeInfo(securityUser);
    }

    @Operation(summary = "아이디/비밀번호 설정", description = "로그인 한 유저의 정보를 불러온다.")
    @PostMapping("/setting/GENERAL")
    public ResponseMessage setEmailAndPassword(Authentication authentication, @RequestBody SetEmailAndPasswordDto setEmailAndPasswordDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.setEmailAndPassword(securityUser, setEmailAndPasswordDto);
        return ResponseMessage.builder()
                .message("아이디/비밀번호 설정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "SNS 계정 연결", description = "SNS 계정을 연결한다.")
    @PostMapping("/connecting/{sns}")
    public ResponseMessage connectSnsAccount(Authentication authentication, @RequestBody SnsAccessToken snsAccessToken, @PathVariable String sns) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.connectSnsAccount(securityUser, snsAccessToken, sns);
        return ResponseMessage.builder()
                .message("SNS 계정 연결에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "애플 SNS 계정 연결", description = "SNS 계정을 연결한다.")
    @PostMapping("/connectingApple")
    public ResponseMessage connectSnsAccount(Authentication authentication, @RequestBody Map<String,Object> appleLoginDto) throws JsonProcessingException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.connectAppleAccount(securityUser, appleLoginDto);
        return ResponseMessage.builder()
                .message("SNS 계정 연결에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "SNS 계정 해지", description = "SNS 계정 연결을 해제한다.")
    @DeleteMapping("/disconnecting/{sns}")
    public ResponseMessage disconnectingSnsAccount(Authentication authentication, @PathVariable String sns) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.disconnectSnsAccount(securityUser, sns);
        return ResponseMessage.builder()
                .message("SNS 계정 연결 해제에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "휴대폰 번호 변경", description = "로그인 한 유저의 비밀번호를 변경한다.")
    @PostMapping("/change/phone")
    public ResponseMessage changePhone(Authentication authentication,
                                       @RequestBody ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.changePhoneNumber(securityUser, changePhoneRequestDto);
        return ResponseMessage.builder()
                .message("휴대폰 번호 변경에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "비밀번호 변경", description = "로그인 한 유저의 비밀번호를 변경한다.")
    @PostMapping("/change/password")
    public ResponseMessage changePassword(Authentication authentication,
                                          @RequestBody ChangePasswordDto changePasswordRequestDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.changePassword(securityUser, changePasswordRequestDto);
        return ResponseMessage.builder()
                .message("비밀번호 변경에 성공하였습니다.")
                .build();
    }

    //TODO : 알림 설정 수정 됨. -> pushCondition 확인필요
    @Operation(summary = "알림 설정 조회", description = "알림/마케팅 수신 정보 설정을 조회한다")
    @GetMapping("/setting")
    public ResponseMessage getAlarmSetting(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        MarketingAlarmResponseDto MarketingDto = userService.getAlarmSetting(securityUser);
        return ResponseMessage.builder()
                .message("알림 설정 조회에 성공하였습니다.")
                .data(MarketingDto)
                .build();
    }

    //TODO : 알림 설정 수정 됨. -> pushCondition 확인필요
    @Operation(summary = "알림 설정", description = "알림/마케팅 수신 정보 설정 동의 여부를 변경한다.")
    @PostMapping("/setting")
    public ResponseMessage changeAlarmSetting(Authentication authentication, @RequestBody MarketingAlarmRequestDto marketingAlarmDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        MarketingAlarmResponseDto changeMarketingDto = userService.changeAlarmSetting(securityUser, marketingAlarmDto);
        return ResponseMessage.builder()
                .message("마케팅 수신 정보 변경에 성공하였습니다.")
                .data(changeMarketingDto)
                .build();
    }

    @Operation(summary = "이름변경", description = "로그인한 유저의 이름이 없을시(애플로그인) 이름을 설정한다.")
    @PostMapping("/setting/name")
    public ResponseMessage changeName(Authentication authentication ,@RequestBody ChangeNameDto changeNameDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.changeName(securityUser, changeNameDto);
        return ResponseMessage.builder()
                .message("유저 이름이 변경되었습니다.")
                .build();
    }

    @Operation(summary = "회원탈퇴 요청", description = "회원 탈퇴를 요청한다.")
    @GetMapping("/withdrawal")
    public ResponseMessage userWithdrawal(Authentication authentication){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.withdrawal(securityUser);
        return ResponseMessage.builder()
                .message("회원 탈퇴 요청이 되었습니다.")
                .build();
    }

    @Operation(summary = "회원탈퇴 요청 취소", description = "회원 탈퇴를 요청을 취소한다.")
    @GetMapping("/withdrawal/cancel")
    public ResponseMessage userWithdrawalCancel(Authentication authentication){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.withdrawalCancel(securityUser);
        return ResponseMessage.builder()
                .message("회원 탈퇴 요청을 취소하였습니다.")
                .build();
    }

    @PostMapping("/save/token")
    @Operation(summary = "FCM 토큰 저장하기", description = "유저정보에 FCM토큰을 저장한다")
    public ResponseMessage tokenSave(Authentication authentication, @RequestBody FcmTokenSaveReqDto fcmTokenSaveReqDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.saveToken(fcmTokenSaveReqDto, securityUser);
        return ResponseMessage.builder()
                .message("토큰 저장 성공!")
                .build();
    }

    @Operation(summary = "이메일 가리기 유저인지 확인", description = "카드 등록시 이메일 가리기 유저인지 확인한다.")
    @GetMapping("/check/hideEmail")
    public ResponseMessage isHideEmail(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userService.isHideEmail(securityUser))
                .message("이메일 가리기 유저 확인에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "첫번째 빌링키 발급하기", description = "나이스페이먼츠 빌링키를 발급한다.")
    @PostMapping("/cards/types/{typeId}")
    public ResponseMessage createBillingKeyFirst(Authentication authentication, @PathVariable Integer typeId, @RequestBody BillingKeyDto billingKeyDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userService.createNiceBillingKeyFirst(securityUser, typeId, billingKeyDto))
                .message("빌링키 발급에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "결제 카드 조회", description = "결제 카드를 조회한다.")
    @GetMapping("/cards")
    public ResponseMessage getCardList(Authentication authentication){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("결제 카드 목록 조회에 성공했습니다.")
                .data(userService.getCardList(securityUser))
                .build();
    }

    @Operation(summary = "디폴트타입 변경", description = "디폴트 타입을 변경한다")
    @PatchMapping("/cards/setting")
    public ResponseMessage patchDefaultCard(Authentication authentication, @RequestBody CreditCardDefaultSettingDto creditCardDefaultSettingDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.patchDefaultCard(securityUser, creditCardDefaultSettingDto);
        return ResponseMessage.builder()
                .message("카드 디폴트타입 변경을 완료했습니다.")
                .build();
    }

    @Operation(summary = "결제 카드 삭제", description = "결제 카드를 삭제한다.")
    @PatchMapping("/cards")
    public ResponseMessage deleteCard(@RequestBody DeleteCreditCardDto deleteCreditCardDto){
        userService.deleteCard(deleteCreditCardDto);
        return ResponseMessage.builder()
                .message("결제 카드를 삭제 했습니다.")
                .build();
    }

    @GetMapping("/payment/password")
    @Operation(summary = "결제 비밀번호 등록 되어 있는지 확인", description = "결제 비밀번호 등록 유무 확인")
    public ResponseMessage isPaymentPassword(Authentication authentication){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userService.isPaymentPassword(securityUser))
                .message("조회 성공!")
                .build();
    }

    @PatchMapping("/payment/password/reset")
    @Operation(summary = "결제 비밀번호 재설정", description = "결제 비밀번호 재설정")
    public ResponseMessage paymentPasswordReset(Authentication authentication, @RequestBody PaymentResetReqDto resetDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.paymentPasswordReset(securityUser, resetDto);
        return ResponseMessage.builder()
                .message("결제 비밀번호 재설정 성공!")
                .build();
    }

    @PostMapping("/preference")
    @Operation(summary = "회원 정보 입력", description = "회원 정보 입력 저장")
    public ResponseMessage userPreferenceSave(Authentication authentication, @RequestBody UserPreferenceDto userPreferenceDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        String message = userService.userPreferenceSave(securityUser, userPreferenceDto);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }

    @GetMapping("/country")
    @Operation(summary = "국가 정보 조회", description = "국가정보를 조회한다.")
    public ResponseMessage getCountry(){
        return ResponseMessage.builder()
                .data(userService.getCountry())
                .message("국가 정보 조회 성공")
                .build();
    }

    @GetMapping("/tags")
    @Operation(summary = "푸드태그 조회", description = "푸드태그 정보를 조회한다.")
    public ResponseMessage getFavoriteCountryFoods(@RequestParam Integer code){
        return ResponseMessage.builder()
                .data(userService.getFavoriteCountryFoods(code))
                .message("조회 성공!")
                .build();
    }

    @GetMapping("/jobs")
    @Operation(summary = "직종 조회", description = "직종을 조회한다.")
    public ResponseMessage getJobType(@RequestParam Integer code){
        return ResponseMessage.builder()
                .data(userService.getJobType(code))
                .message("조회 성공!")
                .build();
    }

    @GetMapping("/preference/foods/images")
    @Operation(summary = "회원정보 입력 중 음식 이미지 불러오기", description = "foodId로 음식 이미지를 불러온다.")
    public ResponseMessage getFoodImage(@RequestParam List<BigInteger> foodId){
        return ResponseMessage.builder()
                .data(userService.getFoodImage(foodId))
                .message("이미지 조회 성공!")
                .build();
    }

    @PostMapping("/payment/password/check")
    @Operation(summary = "결제 비밀번호 확인하기", description = "결제 비밀번호 확인")
    public ResponseMessage checkPaymentPassword(Authentication authentication, @RequestBody SavePaymentPasswordDto savePaymentPasswordDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        String result = userService.checkPaymentPassword(securityUser, savePaymentPasswordDto);
        return ResponseMessage.builder()
                .message(result)
                .build();
    }

//
//    @PostMapping("/payment/password")
//    @Operation(summary = "결제 비밀번호 등록하기", description = "결제 비밀번호 등록")
//    public ResponseMessage savePaymentPassword(Authentication authentication, @RequestBody SavePaymentPasswordDto savePaymentPasswordDto){
//        SecurityUser securityUser = UserUtil.securityUser(authentication);
//        String result = userService.savePaymentPassword(securityUser, savePaymentPasswordDto);
//        return ResponseMessage.builder()
//                .message(result)
//                .build();
//    }
//    @Operation(summary = "결제 카드 등록", description = "결제 카드를 등록한다.")
//    @PostMapping("/cards")
//    public ResponseMessage saveCreditCard(Authentication authentication,
//                                          @RequestBody SaveCreditCardRequestDto saveCreditCardRequestDto) throws IOException, ParseException {
//        SecurityUser securityUser = UserUtil.securityUser(authentication);
//        Integer result = userService.saveCreditCard(securityUser, saveCreditCardRequestDto);
//        if (result == 2){
//            return ResponseMessage.builder()
//                    .message("같은 카드가 이미 등록되어 있습니다.")
//                    .build();
//        }
//        return ResponseMessage.builder()
//                .message("결제 카드 등록에 성공하셨습니다.")
//                .build();
//
//    }
}
