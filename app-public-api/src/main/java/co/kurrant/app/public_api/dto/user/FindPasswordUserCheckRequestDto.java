package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "비밀번호 찾기 요청 DTO")
@Getter
@NoArgsConstructor
public class FindPasswordUserCheckRequestDto {
    private String name;
    private String email;

    @Builder
    public FindPasswordUserCheckRequestDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
