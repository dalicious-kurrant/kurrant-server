package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Schema(description = "장바구니 조회 DTO")
@Getter
@Setter
public class CartDailyFoodDto {
    BigInteger id;
    BigInteger dailyFoodId;
    String name;
    String image;
    String makers;
    String diningType;
    Integer count;
    BigDecimal price;
    Integer membershipDiscountRate;
    Integer membershipDiscountPrice;
    Integer makersDiscountRate;
    Integer makersDiscountPrice;
    Integer periodDiscountRate;
    Integer periodDiscountPrice;
    String serviceDate;
}
