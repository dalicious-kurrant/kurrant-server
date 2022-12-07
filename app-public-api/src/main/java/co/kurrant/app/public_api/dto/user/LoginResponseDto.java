package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "로그인 응답 DTO")
@Builder
@Getter
public class LoginResponseDto {
  private String accessToken;
  private Integer expiresIn;
}
