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
    private String morningLastOrderTime;
    private String lunchLastOrderTime;
    private String dinnerLastOrderTime;
    private BigDecimal supplyPrice;
    private BigDecimal foodPrice;
    private List<String> foodImages;
    private BigDecimal makersDiscountPrice;
    private Integer makersDiscountRate;
    private BigDecimal membershipDiscountPrice;
    private Integer membershipDiscountRate;
    private BigDecimal periodDiscountPrice;
    private Integer periodDiscountRate;
    private List<Integer> foodTags;
    private String description;
    private BigDecimal customPrice;
    private Integer calorie;
    private Integer carbohydrate;
    private Integer fat;
    private Integer protein;

    public MakersFoodDetailDto(String makersName, BigInteger foodId, String foodName, Integer morningCapacity, Integer lunchCapacity,
                               Integer dinnerCapacity, String morningLastOrderTime, String lunchLastOrderTime, String dinnerLastOrderTime, BigDecimal supplyPrice,
                               BigDecimal foodPrice, List<String> foodImages, BigDecimal makersDiscountPrice, Integer makersDiscountRate, BigDecimal membershipDiscountPrice,
                               Integer membershipDiscountRate, BigDecimal periodDiscountPrice, Integer periodDiscountRate, List<Integer> foodTags, String description,
                               BigDecimal customPrice, Integer calorie, Integer carbohydrate, Integer fat, Integer protein) {
        this.makersName = makersName;
        this.foodId = foodId;
        this.foodName = foodName;
        this.morningCapacity = morningCapacity;
        this.lunchCapacity = lunchCapacity;
        this.dinnerCapacity = dinnerCapacity;
        this.morningLastOrderTime = morningLastOrderTime;
        this.lunchLastOrderTime = lunchLastOrderTime;
        this.dinnerLastOrderTime = dinnerLastOrderTime;
        this.supplyPrice = supplyPrice;
        this.foodPrice = foodPrice;
        this.foodImages = foodImages;
        this.makersDiscountPrice = makersDiscountPrice;
        this.makersDiscountRate = makersDiscountRate;
        this.membershipDiscountPrice = membershipDiscountPrice;
        this.membershipDiscountRate = membershipDiscountRate;
        this.periodDiscountPrice = periodDiscountPrice;
        this.periodDiscountRate = periodDiscountRate;
        this.foodTags = foodTags;
        this.description = description;
        this.customPrice = customPrice;
        this.calorie = calorie;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.protein = protein;
    }
}
