package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "아이디 찾기 요청 DTO")
@Getter
@NoArgsConstructor
public class FindIdRequestDto {
    public String phone;

    @Builder
    public FindIdRequestDto(String phone) {
        this.phone = phone;
    }
}
