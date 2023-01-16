package co.dalicious.client.core.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginTokenDto {
    String accessToken;
    String refreshToken;
    Long accessTokenExpiredIn;

    @Builder
    public LoginTokenDto(String accessToken, String refreshToken, Long accessTokenExpiredIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiredIn = accessTokenExpiredIn;
    }
}
