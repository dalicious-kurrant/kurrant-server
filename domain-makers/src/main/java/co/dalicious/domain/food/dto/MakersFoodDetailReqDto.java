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
    private List<String> images;
}
