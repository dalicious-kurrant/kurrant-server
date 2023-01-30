package co.dalicious.client.oauth;

import co.dalicious.domain.user.entity.enums.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface SnsLoginService {
    SnsLoginResponseDto getSnsLoginUserInfo(Provider provider, String accessToken);
    SnsLoginResponseDto getNaverLoginUserInfo(String accessToken);
    SnsLoginResponseDto getKakaoLoginUserInfo(String accessToken);
    SnsLoginResponseDto getGoogleLoginUserInfo(String accessToken);
    SnsLoginResponseDto getAppleLoginUserInfo(AppleLoginDto appleLoginDto) throws JsonProcessingException;
    SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken);
}
