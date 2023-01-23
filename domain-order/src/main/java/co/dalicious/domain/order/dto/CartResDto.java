package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "장바구니 응답 DTO")
public class CartResDto {
    List<SpotCarts> spotCarts;
    BigDecimal deliveryFee;
    BigDecimal userPoint;

    @Builder
    public CartResDto(List<SpotCarts> spotCarts, BigDecimal deliveryFee, BigDecimal userPoint) {
        this.spotCarts = spotCarts;
        this.deliveryFee = deliveryFee;
        this.userPoint = userPoint;
    }

    @Getter
    @Setter
    public static class SpotCarts {
        private BigInteger spotId;
        private String spotName;
        List<CartDailyFoodDto> cartDailyFoodDtoList;

        @Builder
        public SpotCarts(BigInteger spotId, String spotName, List<CartDailyFoodDto> cartDailyFoodDtoList) {
            this.spotId = spotId;
            this.spotName = spotName;
            this.cartDailyFoodDtoList = cartDailyFoodDtoList;
        }
    }
}
