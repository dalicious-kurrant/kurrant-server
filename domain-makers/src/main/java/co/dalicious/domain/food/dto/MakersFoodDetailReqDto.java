package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class MakersFoodDetailReqDto {
    private BigInteger foodId;
    private Integer makersDiscountRate;
    private Integer periodDiscountRate;
    private List<Integer> foodTags;
}
