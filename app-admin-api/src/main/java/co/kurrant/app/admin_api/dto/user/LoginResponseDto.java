package co.kurrant.app.admin_api.dto.user;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.kurrant.app.admin_api.model.Admin;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
  private String accessToken;
  private Long expiresIn;
  private String name;

  public static LoginResponseDto create(LoginTokenDto loginTokenDto, Admin admin) {
    return LoginResponseDto.builder()
            .accessToken(loginTokenDto.getAccessToken())
            .expiresIn(loginTokenDto.getAccessTokenExpiredIn())
            .name(admin.getUsername())
            .build();
  }
}
