package co.kurrant.app.client_api.dto;

import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "로그인 BODY")
@Setter
@Getter
@ToString
public class LoginRequestDto {

  @Schema(description = "사용자 아이디(고객사 코드)")
  @NotBlank
  private String code;

  @Schema(description = "사용자 비밀번호")
  @NotBlank
  private String password;

}
