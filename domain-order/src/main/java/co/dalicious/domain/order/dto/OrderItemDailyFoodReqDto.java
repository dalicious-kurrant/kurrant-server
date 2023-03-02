package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "식사 주문하기 요청 OTO")
public class OrderItemDailyFoodReqDto {
    @Schema(description = "토스 paymentKey")
    private String paymentKey;
    @Schema(description = "주문코드")
    private String orderId;
    @Schema(description = "총 주문 금액")
    private Integer amount;
    @Schema(description = "spotId/cartDailyFoodDtoList/totalPrice/supportPrice/deliveryFee/userPoint")
    private OrderItems orderItems;
}
