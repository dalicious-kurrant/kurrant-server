package co.dalicious.client.oauth;

import co.dalicious.domain.user.entity.Provider;

public interface SnsLoginService {
    SnsLoginResponseDto getSnsLoginUserInfo(Provider provider, String accessToken);
    SnsLoginResponseDto getNaverLoginUserInfo(String accessToken);
    SnsLoginResponseDto getKakaoLoginUserInfo(String accessToken);
    SnsLoginResponseDto getGoogleLoginUserInfo(String accessToken);
    SnsLoginResponseDto getAppleLoginUserInfo(String accessToken);
    SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken);
}
