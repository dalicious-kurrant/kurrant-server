package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Schema(description = "식단 응답 DTO")
@Getter
@Setter
public class DailyFoodDto {
    private BigInteger id;
    private Integer diningType;
    private BigInteger foodId;
    private String foodName;
    private Boolean isSoldOut;
    private BigInteger spotId;
    private String serviceDate;
    private String makersName;
    private String spicy;
    private String image;
    private String description;
    private BigDecimal price;
    private BigDecimal membershipDiscountPrice;
    private Integer membershipDiscountRate;
    private BigDecimal makersDiscountPrice;
    private Integer makersDiscountRate;
    private BigDecimal periodDiscountPrice;
    private Integer periodDiscountRate;
}
