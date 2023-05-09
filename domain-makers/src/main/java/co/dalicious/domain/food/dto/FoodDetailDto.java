package co.dalicious.domain.food.dto;

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
    private Integer capacity;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal membershipDiscountedPrice;
    private Integer membershipDiscountedRate;
    private BigDecimal makersDiscountedPrice;
    private Integer makersDiscountedRate;
    private BigDecimal periodDiscountedPrice;
    private Integer periodDiscountedRate;
    private List<String> imageList;
    private String spicy;
    private String vegan;
    private String description;
    private List<OriginDto> origins;
    private List<String> allergies;
}
