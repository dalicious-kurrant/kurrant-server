package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "멤버십 정기결제 요청 DTO")
public class OrderMembershipReqDto {
    @Schema(description = "결제 타입")
    private Integer paymentType;
    @Schema(description = "결제 카드 ID")
    private BigInteger cardId;
    @Schema(description = "구독 타입")
    private Integer subscriptionType;
    @Schema(description = "기본가격")
    private BigDecimal defaultPrice;
    @Schema(description = "연구독 할인가격")
    private BigDecimal yearDescriptionDiscountPrice;
    @Schema(description = "기간 할인 가격")
    private BigDecimal periodDiscountPrice;
    @Schema(description = "총 가격")
    private BigDecimal totalPrice;
}
