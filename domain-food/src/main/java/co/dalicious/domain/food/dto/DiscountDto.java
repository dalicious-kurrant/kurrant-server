package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "정기식사 가격/할인 응답 DTO")
public class DiscountDto {
    private BigDecimal price;
    private BigDecimal membershipDiscountedPrice;
    private Integer membershipDiscountedRate;
    private BigDecimal makersDiscountedPrice;
    private Integer makersDiscountedRate;
    private BigDecimal periodDiscountedPrice;
    private Integer periodDiscountedRate;
}
