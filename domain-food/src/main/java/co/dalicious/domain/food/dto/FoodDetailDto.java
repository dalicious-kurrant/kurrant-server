package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.util.OriginDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "식품 상세정보 DTO")
@Getter
@Setter
public class FoodDetailDto {
    private String makersName;
    private String name;
    private BigDecimal price;
    private BigDecimal membershipDiscountedPrice;
    private Integer membershipDiscountedRate;
    private BigDecimal makersDiscountedPrice;
    private Integer makersDiscountedRate;
    private BigDecimal periodDiscountedPrice;
    private Integer periodDiscountedRate;
    private String image;
    private String spicy;
    private String description;
    private List<OriginDto> origins;
}
