package co.kurrant.app.makers_api.dto.user;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.domain.food.entity.Makers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Schema(description = "로그인 응답 DTO")
@Builder
@Getter
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String name;
    private BigInteger makersId;

    public static LoginResponseDto create(LoginTokenDto loginTokenDto, Makers makers) {
        return LoginResponseDto.builder()
                .accessToken(loginTokenDto.getAccessToken())
                .refreshToken(loginTokenDto.getRefreshToken())
                .expiresIn(loginTokenDto.getAccessTokenExpiredIn())
                .name(makers.getName())
                .makersId(makers.getId())
                .build();
    }

}