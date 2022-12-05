package co.kurrant.app.public_api.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SetEmailAndPasswordDto {
    String email;
    String password;
    String passwordCheck;

    @Builder
    public SetEmailAndPasswordDto(String email, String password, String passwordCheck) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }
}
