package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "소셜 로그인 회원 이메일/비밀번호 설정 DTO")
@Getter
@NoArgsConstructor
public class SetEmailAndPasswordDto {
    private String email;
    private String password;
    private String passwordCheck;

    @Builder
    public SetEmailAndPasswordDto(String email, String password, String passwordCheck) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }
}
