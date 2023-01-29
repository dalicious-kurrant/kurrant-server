package co.dalicious.client.oauth;

import co.dalicious.domain.user.entity.enums.Provider;

public interface SnsLoginService {
    SnsLoginResponseDto getSnsLoginUserInfo(Provider provider, String accessToken);
    SnsLoginResponseDto getNaverLoginUserInfo(String accessToken);
    SnsLoginResponseDto getKakaoLoginUserInfo(String accessToken);
    SnsLoginResponseDto getGoogleLoginUserInfo(String accessToken);
    SnsLoginResponseDto getAppleLoginUserInfo(AppleLoginDto appleLoginDto);
    SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken);
}
