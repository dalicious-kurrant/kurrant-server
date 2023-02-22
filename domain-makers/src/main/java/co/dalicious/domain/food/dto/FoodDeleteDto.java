package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class FoodDeleteDto {
    private Integer foodStatus;
    private List<BigInteger> foodId;
}
