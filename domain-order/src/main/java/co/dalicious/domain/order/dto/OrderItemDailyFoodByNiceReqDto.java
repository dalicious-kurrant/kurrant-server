package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "식사 주문하기 요청 DTO(나이스빌링)")
public class OrderItemDailyFoodByNiceReqDto {
    private BigInteger cardId;
    @Schema(description = "주문코드")
    private String orderId;
    @Schema(description = "총 주문 금액")
    private Integer amount;
    @Schema(description = "spotId/cartDailyFoodDtoList/totalPrice/supportPrice/deliveryFee/userPoint")
    private OrderItems orderItems;
    private String orderName;

}
