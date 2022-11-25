package co.kurrant.app.public_api.dto.user;

import co.dalicious.client.core.annotation.validation.ValidEnum;
import co.dalicious.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원가입 시 프론트에서 보내주는 요청을 담아주는 Dto")
@Setter
@Getter
public class SignUpRequestDto {
    String email;
    String password;
    String passwordCheck;
    String phone;
    String name;
}
