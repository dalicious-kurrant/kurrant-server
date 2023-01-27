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
    @Schema(description = "spotId")
    BigInteger spotId;
    @Schema(description = "결제할 CardId")
    BigInteger cardId;
    @Schema(description = "장바구니 목록")
    List<CartDailyFoodDto> cartDailyFoodDtoList;
    @Schema(description = "총 가격")
    BigDecimal totalPrice;
    @Schema(description = "일일 지원금")
    BigDecimal supportPrice;
    @Schema(description = "배송바")
    BigDecimal deliveryFee;
    @Schema(description = "사용할 포인트")
    BigDecimal userPoint;

}
