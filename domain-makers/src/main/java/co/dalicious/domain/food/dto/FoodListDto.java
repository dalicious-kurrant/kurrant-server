package co.dalicious.domain.food.dto;

import co.dalicious.system.util.enums.FoodTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
public class FoodListDto {

    private BigInteger id;
    private String makersName;
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
            BigInteger id, String makersName, String foodName, String foodImage, String foodStatus,
            BigDecimal defaultPrice, Integer makersDiscount, Integer eventDiscount, BigDecimal resultPrice,
            String description, List<String> foodTags) {

        this.id = id;
        this.makersName = makersName;
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
