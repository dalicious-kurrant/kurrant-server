package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "멤버십 정기결제 요청 DTO")
public class OrderMembershipReqDto {
    private Integer paymentType;
    private Integer subscriptionType;
    private BigDecimal defaultPrice;
    private BigDecimal yearDescriptionDiscountPrice;
    private BigDecimal periodDiscountPrice;
    private BigDecimal totalPrice;
}
