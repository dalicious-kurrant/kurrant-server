package co.dalicious.client.oauth;

public interface SnsLoginService {
    SnsLoginResponseDto getNaverLoginUserInfo(String accessToken);
    SnsLoginResponseDto getKakaoLoginUserInfo(String accessToken);
    SnsLoginResponseDto getGoogleLoginUserInfo(String accessToken);
    SnsLoginResponseDto getAppleLoginUserInfo(String accessToken);
    SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken);
}
