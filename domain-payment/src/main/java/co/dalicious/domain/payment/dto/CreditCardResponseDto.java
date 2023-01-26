package co.dalicious.domain.payment.dto;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Schema(description = "CreditCardInfo 저장용 DTO")
@Setter
public class CreditCardResponseDto {
    @ApiParam(value = "카드 ID")
    private BigInteger id;
    @ApiParam(value = "카드 번호")
    private String cardNumber;
    @ApiParam(value = "카드 회사명")
    private String cardCompany;
    @ApiParam(value = "카드 유형(개인/법인)")
    private String ownerType;
    @ApiParam(value = "카드 유형(체크/신용)")
    private String cardType;
    @ApiParam(value = "디폴트타입여부(1:기본결제카드, 2:멤버십카드, 0:아무것도 아닌카드")
    private Integer defaultType;

}
