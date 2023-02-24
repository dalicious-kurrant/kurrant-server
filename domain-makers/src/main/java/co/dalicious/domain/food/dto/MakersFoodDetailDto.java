package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
public class MakersFoodDetailDto {

    private String makersName;
    private BigInteger foodId;
    private String foodName;
    private Integer morningCapacity;
    private Integer lunchCapacity;
    private Integer dinnerCapacity;
    private BigDecimal foodPrice;
    private List<String> foodImages;
    private BigDecimal makersDiscountPrice;
    private Integer makersDiscountRate;
    private BigDecimal periodDiscountPrice;
    private Integer periodDiscountRate;
    private List<Integer> foodTags;
    private String description;
    private BigDecimal customPrice;

    public MakersFoodDetailDto(String makersName, BigInteger foodId, String foodName, Integer morningCapacity, Integer lunchCapacity, Integer dinnerCapacity, BigDecimal foodPrice, List<String> foodImages, BigDecimal makersDiscountPrice, Integer makersDiscountRate, BigDecimal periodDiscountPrice, Integer periodDiscountRate, List<Integer> foodTags, String description, BigDecimal customPrice) {
        this.makersName = makersName;
        this.foodId = foodId;
        this.foodName = foodName;
        this.morningCapacity = morningCapacity;
        this.lunchCapacity = lunchCapacity;
        this.dinnerCapacity = dinnerCapacity;
        this.foodPrice = foodPrice;
        this.foodImages = foodImages;
        this.makersDiscountPrice = makersDiscountPrice;
        this.makersDiscountRate = makersDiscountRate;
        this.periodDiscountPrice = periodDiscountPrice;
        this.periodDiscountRate = periodDiscountRate;
        this.foodTags = foodTags;
        this.description = description;
        this.customPrice = customPrice;
    }
}
