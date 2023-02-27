package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class FoodStatusUpdateDto {
    private BigInteger foodId;
    private Integer foodStatus;
}
