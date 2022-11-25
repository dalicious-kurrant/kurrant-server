package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "유저 생성 Dto")
@Setter
@Getter
public class UserDto {
    String email;
    byte[] password;
    String phone;
    String name;
}

