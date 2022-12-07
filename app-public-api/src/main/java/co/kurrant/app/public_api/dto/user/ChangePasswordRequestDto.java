package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "비밀번호 변경 요청 DTO")
@Getter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    private String currantPassword;
    private String newPassword;
    private String newPasswordCheck;

    @Builder
    public ChangePasswordRequestDto(String currantPassword, String newPassword, String newPasswordCheck) {
        this.currantPassword = currantPassword;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }
}
