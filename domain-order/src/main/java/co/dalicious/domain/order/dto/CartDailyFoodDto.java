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
        BigInteger id;
        BigInteger dailyFoodId;
        Integer status;
        Integer capacity;
        String name;
        String image;
        String makers;
        Integer count;
        BigDecimal price;
        BigDecimal discountedPrice;
        Integer membershipDiscountRate;
        BigDecimal membershipDiscountPrice;
        Integer makersDiscountRate;
        BigDecimal makersDiscountPrice;
        Integer periodDiscountRate;
        BigDecimal periodDiscountPrice;
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
