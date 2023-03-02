package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderItems {
    BigInteger spotId;
    List<CartDailyFoodDto> cartDailyFoodDtoList;
    BigDecimal totalPrice;
    BigDecimal supportPrice;
    BigDecimal deliveryFee;
    BigDecimal userPoint;
}
