package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class FoodListDto {
    private BigInteger foodId;
    private String makersName;
    private BigInteger makersId;
    private String foodName;
    private String foodImage;
    private String foodStatus;
    private BigDecimal defaultPrice;
    private Integer makersDiscount;
    private Integer eventDiscount;
    private BigDecimal resultPrice;
    private String description;
    private List<String> foodTags;

    public FoodListDto(
            BigInteger foodId, String makersName, BigInteger makersId, String foodName, String foodImage, String foodStatus,
            BigDecimal defaultPrice, Integer makersDiscount, Integer eventDiscount, BigDecimal resultPrice,
            String description, List<String> foodTags) {

        this.foodId = foodId;
        this.makersName = makersName;
        this.makersId = makersId;
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.foodStatus = foodStatus;
        this.defaultPrice = defaultPrice;
        this.makersDiscount = makersDiscount;
        this.eventDiscount = eventDiscount;
        this.resultPrice = resultPrice;
        this.description = description;
        this.foodTags = foodTags;
    }

}
