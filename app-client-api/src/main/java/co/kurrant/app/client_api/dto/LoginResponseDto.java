package co.kurrant.app.client_api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.domain.client.entity.Corporation;
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
  private BigInteger groupId;

  public static LoginResponseDto create(LoginTokenDto loginTokenDto, Corporation corporation) {
    return LoginResponseDto.builder()
            .accessToken(loginTokenDto.getAccessToken())
            .refreshToken(loginTokenDto.getRefreshToken())
            .expiresIn(loginTokenDto.getAccessTokenExpiredIn())
            .name(corporation.getName())
            .groupId(corporation.getId())
            .build();
  }

}
