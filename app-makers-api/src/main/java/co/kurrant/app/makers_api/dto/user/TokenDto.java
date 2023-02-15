package co.kurrant.app.makers_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Schema(description = "엑세스 토큰 재발급 요청 DTO")
public class TokenDto {
    @NotEmpty(message = "Access Token을 입력해주세요.")
    private String accessToken;

    @NotEmpty(message = "Refresh Token을 입력해주세요.")
    private String refreshToken;
}
