package co.kurrant.app.public_api.dto.user;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Schema(description = "결제 카드 등록 요청 DTO")
@Getter
@Setter
public class SaveCreditCardRequestDto {
    @ApiParam(value = "카드번호")
    private String cardNumber;
    @ApiParam(value = "유효기간 년")
    private String expirationYear;
    @ApiParam(value = "유효기간 월")
    private String expirationMonth;
    @ApiParam(value = "카드 비밀번호 앞 두자리")
    private String cardPassword;
    @ApiParam(value = "생년월일")
    private String identityNumber;
    @ApiParam(value = "카드 검증번호(CVC)")
    private String cardVaildationCode;
}