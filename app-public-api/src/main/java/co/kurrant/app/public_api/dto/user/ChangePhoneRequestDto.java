package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "마이페이지 휴대폰 번호 인증 및 변경 요청 DTO")
@Getter
@NoArgsConstructor
public class ChangePhoneRequestDto {
    private String phone;
    private String key;

    @Builder
    public ChangePhoneRequestDto(String phone, String key) {
        this.phone = phone;
        this.key = key;
    }
}
