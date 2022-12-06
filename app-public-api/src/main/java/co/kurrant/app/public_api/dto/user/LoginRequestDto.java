package co.kurrant.app.public_api.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
  @NotBlank()
  private String email;

  @NotBlank()
  private String password;
}
