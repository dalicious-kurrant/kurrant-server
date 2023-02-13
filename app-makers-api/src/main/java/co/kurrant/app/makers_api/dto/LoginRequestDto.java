package co.kurrant.app.makers_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank()
    private String code;

    @NotBlank()
    private String password;
}

