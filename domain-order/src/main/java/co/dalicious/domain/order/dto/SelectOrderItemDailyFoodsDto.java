package co.dalicious.domain.order.dto;

import co.dalicious.domain.order.entity.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;

@Getter
@Setter
public class SelectOrderItemDailyFoodsDto {
    private BigInteger orderItemDailyFoodId;
    private LocalTime deliveryTime;
    private String makers;
    private String foodName;
    private Integer count;
    private BigDecimal price;
    private BigDecimal supplyPrice;
    private OrderStatus orderStatus;
}
