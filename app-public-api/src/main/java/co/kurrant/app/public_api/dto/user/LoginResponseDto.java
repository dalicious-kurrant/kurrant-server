package co.kurrant.app.public_api.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
  private String accessToken;
  private Integer expiresIn;
}
