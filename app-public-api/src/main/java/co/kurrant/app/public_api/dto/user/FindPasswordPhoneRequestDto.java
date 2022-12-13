package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "비밀번호 찾기시 휴대폰 인증을 통해 비밀번호를 재설정을 위한 요청 DTO")
@Getter
@NoArgsConstructor
public class FindPasswordPhoneRequestDto {
    private String phone;
    private String password;
    private String passwordCheck;

    @Builder
    public FindPasswordPhoneRequestDto(String phone, String password, String passwordCheck) {
        this.phone = phone;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }
}
