package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "멤버십 정기결제 응답 DTO")
public class OrderMembershipResDto {
    private Integer subscriptionType;
    private BigDecimal defaultPrice;
    private BigDecimal yearDescriptionDiscountPrice;
    private BigDecimal periodDiscountPrice;
    private BigDecimal totalPrice;

    @Builder
    public OrderMembershipResDto(Integer subscriptionType, BigDecimal defaultPrice, BigDecimal yearDescriptionDiscountPrice, BigDecimal periodDiscountPrice, BigDecimal totalPrice) {
        this.subscriptionType = subscriptionType;
        this.defaultPrice = defaultPrice;
        this.yearDescriptionDiscountPrice = yearDescriptionDiscountPrice;
        this.periodDiscountPrice = periodDiscountPrice;
        this.totalPrice = totalPrice;
    }
}
