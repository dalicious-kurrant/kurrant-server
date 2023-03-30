package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "결제 비밀번호 등록 요청 DTO")
public class SavePaymentPasswordDto {

    private String payNumber;

}
