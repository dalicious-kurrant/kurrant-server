package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class MakersFoodDetailDto {
    private String makersName;
    private String foodGroup;
    private BigInteger foodId;
    private String foodName;
    private Integer morningCapacity;
    private Integer lunchCapacity;
    private Integer dinnerCapacity;
    private String morningLastOrderTime;
    private String lunchLastOrderTime;
    private String dinnerLastOrderTime;
    private BigDecimal supplyPrice;
    private BigDecimal defaultPrice;
    private List<String> foodImages;
    private List<String> introImages;
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

    public MakersFoodDetailDto(String makersName, String foodGroup, BigInteger foodId, String foodName, Integer morningCapacity, Integer lunchCapacity, Integer dinnerCapacity, String morningLastOrderTime, String lunchLastOrderTime, String dinnerLastOrderTime, BigDecimal supplyPrice, BigDecimal defaultPrice, List<String> foodImages, List<String> introImages, BigDecimal makersDiscountPrice, Integer makersDiscountRate, BigDecimal membershipDiscountPrice, Integer membershipDiscountRate, BigDecimal periodDiscountPrice, Integer periodDiscountRate, List<Integer> foodTags, String description, BigDecimal customPrice, Integer calorie, Integer carbohydrate, Integer fat, Integer protein) {
        this.makersName = makersName;
        this.foodGroup = foodGroup;
        this.foodId = foodId;
        this.foodName = foodName;
        this.morningCapacity = morningCapacity;
        this.lunchCapacity = lunchCapacity;
        this.dinnerCapacity = dinnerCapacity;
        this.morningLastOrderTime = morningLastOrderTime;
        this.lunchLastOrderTime = lunchLastOrderTime;
        this.dinnerLastOrderTime = dinnerLastOrderTime;
        this.supplyPrice = supplyPrice;
        this.defaultPrice = defaultPrice;
        this.foodImages = foodImages;
        this.introImages = introImages;
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
