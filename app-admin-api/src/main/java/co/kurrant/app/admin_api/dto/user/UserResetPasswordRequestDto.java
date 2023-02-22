package co.kurrant.app.admin_api.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "비밀번호 리셋 요청 DTO")
public class UserResetPasswordRequestDto {
    private BigInteger userId;
}
