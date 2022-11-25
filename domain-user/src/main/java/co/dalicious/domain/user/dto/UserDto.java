package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "유저 생성 Dto")
@NoArgsConstructor
@Getter
public class UserDto {
    String email;
    String password;
    String phone;
    String name;

    @Builder
    public UserDto(String email, String password, String phone, String name) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }
}

