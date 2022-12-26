package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.domain.user.entity.User;
import java.util.Date;
import co.kurrant.app.public_api.dto.user.*;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
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
    void disconnectSnsAccount(HttpServletRequest httpServletRequest, String provider);
    // 휴대폰 번호 변경
    void changePhoneNumber(HttpServletRequest httpServletRequest, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;
    // 비밀번호 변경
    void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto);
    // 이메일/비밀번호 설정
    void setEmailAndPassword(HttpServletRequest httpServletRequest, SetEmailAndPasswordDto setEmailAndPasswordDto);
    // 알람/마케팅 설정 변경
    ChangeMarketingDto changeAlarmSetting(HttpServletRequest httpServletRequest, Boolean isMarketingInfoAgree,
                                          Boolean isMarketingAlarmAgree, Boolean isOrderAlarmAgree);
    // 유저 정보 가져오기
    UserInfoDto getUserInfo(HttpServletRequest httpServletRequest);
    User findAll();

    OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate);

    void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto);
}
