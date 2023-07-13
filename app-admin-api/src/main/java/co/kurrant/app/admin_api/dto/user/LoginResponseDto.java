package co.kurrant.app.admin_api.dto.user;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.kurrant.app.admin_api.model.Admin;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
public class LoginResponseDto {
  private String accessToken;
  private Long expiresIn;
  private String name;

  public LoginResponseDto (LoginTokenDto loginTokenDto, Admin admin) {
    this.accessToken = loginTokenDto.getAccessToken();
    this.expiresIn = loginTokenDto.getAccessTokenExpiredIn();
    this.name = admin.getUsername();
  }
  @Builder
  public LoginResponseDto(String accessToken, Long expiresIn, String name) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.name = name;
  }
}
