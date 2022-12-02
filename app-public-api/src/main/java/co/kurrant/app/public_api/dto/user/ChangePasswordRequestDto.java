package co.kurrant.app.public_api.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    String currantPassword;
    String newPassword;
    String newPasswordCheck;

    @Builder
    public ChangePasswordRequestDto(String currantPassword, String newPassword, String newPasswordCheck) {
        this.currantPassword = currantPassword;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }
}
