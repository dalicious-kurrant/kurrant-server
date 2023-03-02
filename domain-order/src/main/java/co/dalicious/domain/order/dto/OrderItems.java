package co.dalicious.domain.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class OrderItems {
    BigInteger spotId;
    List<CartDailyFoodDto> cartDailyFoodDtoList;
    BigDecimal totalPrice;
    BigDecimal supportPrice;
    BigDecimal deliveryFee;
    BigDecimal userPoint;
}
