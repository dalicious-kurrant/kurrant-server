package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
@Getter
@Setter
public class LoginRequestDto {
  @NotBlank()
  private String email;

  @NotBlank()
  private String password;

  private String fcmToken;
}
