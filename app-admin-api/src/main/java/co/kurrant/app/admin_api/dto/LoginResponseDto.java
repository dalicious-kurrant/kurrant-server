package co.kurrant.app.admin_api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
  private String accessToken;
  private Integer expiresIn;
}
