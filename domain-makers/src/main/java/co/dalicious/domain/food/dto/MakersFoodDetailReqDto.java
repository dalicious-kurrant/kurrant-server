package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class MakersFoodDetailReqDto {
    private BigInteger foodId;
    private BigInteger foodGroupId;
    private BigDecimal supplyPrice;
    private BigDecimal defaultPrice;
    private Integer membershipDiscountRate;
    private Integer makersDiscountRate;
    private Integer periodDiscountRate;
    private String description;
    private List<Integer> foodTags;
    private BigDecimal customPrice;
    private Integer morningCapacity;
    private Integer lunchCapacity;
    private Integer dinnerCapacity;

    private String morningLastOrderTime;
    private String lunchLastOrderTime;
    private String dinnerLastOrderTime;
    private List<String> foodImages;
    private List<String> introImages;
    private Integer calorie;
    private Integer fat;
    private Integer protein;
    private Integer carbohydrate;
}
