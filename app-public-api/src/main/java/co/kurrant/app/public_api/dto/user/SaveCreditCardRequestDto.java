package co.kurrant.app.public_api.dto.user;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "결제 카드 등록 요청 DTO")
@Getter
@Setter
public class SaveCreditCardRequestDto {
    @Schema(description = "카드번호")
    private String cardNumber;
    @Schema(description = "유효기간 년")
    private String expirationYear;
    @Schema(description = "유효기간 월")
    private String expirationMonth;
    @Schema(description = "카드 비밀번호 앞 두자리")
    private String cardPassword;
    @Schema(description = "생년월일 8자리")
    private String identityNumber;
    @Schema(description = "카드 검증번호 (CVC)")
    private String cardVaildationCode;
    @Schema(description = "디폴트타입(0:아무것도아님, 1:기본결제카드, 2:멤버십 기본 결제 카드)")
    private Integer defaultType;
}
