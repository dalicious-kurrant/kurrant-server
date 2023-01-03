package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "토큰 발급 응답 DTO")
@Builder
@Getter
public class TokenResponseDto {
  private String accessToken;
  private Integer expiresIn;
}
