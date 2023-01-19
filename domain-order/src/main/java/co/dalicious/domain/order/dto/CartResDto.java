package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "장바구니 응답 DTO")
public class CartResDto {
    List<CartDailyFoodDto> cartDailyFoodDtoList;
    BigDecimal deliveryFee;
    BigDecimal userPoint;

    @Builder
    public CartResDto(List<CartDailyFoodDto> cartDailyFoodDtoList, BigDecimal deliveryFee, BigDecimal userPoint) {
        this.cartDailyFoodDtoList = cartDailyFoodDtoList;
        this.deliveryFee = deliveryFee;
        this.userPoint = userPoint;
    }
}
