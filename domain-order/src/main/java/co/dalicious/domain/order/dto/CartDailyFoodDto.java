package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Schema(description = "장바구니 조회 DTO")
@Getter
@Setter
@NoArgsConstructor
public class CartDailyFoodDto {
    String serviceDate;
    String diningType;
    BigDecimal supportPrice;
    BigDecimal deliveryFee;
    List<DailyFood> cartDailyFoods;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DailyFood {
        private BigInteger id;
        private BigInteger dailyFoodId;
        private String deliveryTime;
        private Integer status;
        private Integer capacity;
        private String name;
        private String image;
        private String makers;
        private Integer count;
        private BigDecimal price;
        private BigDecimal discountedPrice;
        private Integer membershipDiscountRate;
        private BigDecimal membershipDiscountPrice;
        private Integer makersDiscountRate;
        private BigDecimal makersDiscountPrice;
        private Integer periodDiscountRate;
        private BigDecimal periodDiscountPrice;
    }

    @Builder
    public CartDailyFoodDto(String serviceDate, String diningType, BigDecimal supportPrice, BigDecimal deliveryFee, List<DailyFood> cartDailyFoods) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.supportPrice = supportPrice;
        this.deliveryFee = deliveryFee;
        this.cartDailyFoods = cartDailyFoods;
    }
}
