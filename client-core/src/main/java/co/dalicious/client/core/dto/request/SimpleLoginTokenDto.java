package co.dalicious.client.core.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleLoginTokenDto {
    String accessToken;
    Long accessTokenExpiredIn;

    @Builder
    public SimpleLoginTokenDto(String accessToken, Long accessTokenExpiredIn) {
        this.accessToken = accessToken;
        this.accessTokenExpiredIn = accessTokenExpiredIn;
    }
}
