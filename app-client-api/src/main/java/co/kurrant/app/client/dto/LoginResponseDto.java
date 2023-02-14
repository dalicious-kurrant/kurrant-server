package co.kurrant.app.client.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "2차 로그인 응답")
@Getter
@Builder
public class LoginResponseDto {
  @Schema(description = "접근 토큰")
  @NotBlank
  private String accessToken;

  @Schema(description = "리프레시용 토큰")
  @NotBlank
  private String refreshToken;

  @Schema(description = "아이디 토큰")
  @NotBlank
  private String idToken;

  @Schema(description = "토큰 만료시간")
  @NotNull
  private Number expiresIn;

  @Schema(description = "토큰 유형, Bearer 고정이다.", defaultValue = "Bearer")
  @NotBlank
  private String tokenType;
}
