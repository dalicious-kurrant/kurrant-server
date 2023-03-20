package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "나이스 빌링키 발급을 위한 요청 DTO")
public class OrderCreateBillingKeyReqDto {
    private String cardNumber;
    private String expirationYear;
    private String expirationMonth;
    private String cardPassword;
    private String identityNumber;

}
