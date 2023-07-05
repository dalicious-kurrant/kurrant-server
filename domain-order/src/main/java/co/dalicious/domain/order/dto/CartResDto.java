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
    BigDecimal userPoint;

    @Builder
    public CartResDto(List<SpotCarts> spotCarts, BigDecimal userPoint) {
        this.spotCarts = spotCarts;
        this.userPoint = userPoint;
    }

    @Getter
    @Setter
    public static class SpotCarts {
        private BigInteger spotId;
        private String spotName;
        private String groupName;
        private Integer clientStatus;
        private String phone;
        private List<CartDailyFoodDto> cartDailyFoodDtoList;

        @Builder
        public SpotCarts(BigInteger spotId, String spotName, String groupName, Integer clientStatus, List<CartDailyFoodDto> cartDailyFoodDtoList, String phone) {
            this.spotId = spotId;
            this.spotName = spotName;
            this.groupName = groupName;
            this.clientStatus = clientStatus;
            this.cartDailyFoodDtoList = cartDailyFoodDtoList;
            this.phone = phone;
        }
    }
}
