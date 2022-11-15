package shop.allof.app.adminapi.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
  private String accessToken;
  private Integer expiresIn;
}
