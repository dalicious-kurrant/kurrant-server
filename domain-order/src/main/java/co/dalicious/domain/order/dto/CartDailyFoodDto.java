package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Schema(description = "장바구니 조회 DTO")
@Getter
@Setter
public class CartDailyFoodDto {
    String serviceDate;
    String diningType;
    BigDecimal supportPrice;
    List<DailyFood> cartDailyFoods;

    @Getter
    @Setter
    public static class DailyFood {
        BigInteger id;
        BigInteger dailyFoodId;
        String name;
        String image;
        String makers;
        Integer count;
        BigDecimal price;
        Integer membershipDiscountRate;
        Integer membershipDiscountPrice;
        Integer makersDiscountRate;
        Integer makersDiscountPrice;
        Integer periodDiscountRate;
        Integer periodDiscountPrice;
    }

    @Builder
    public CartDailyFoodDto(String serviceDate, String diningType, BigDecimal supportPrice, List<DailyFood> cartDailyFoods) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.supportPrice = supportPrice;
        this.cartDailyFoods = cartDailyFoods;
    }
}
