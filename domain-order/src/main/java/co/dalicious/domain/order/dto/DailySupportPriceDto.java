package co.dalicious.domain.order.dto;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DailySupportPriceDto {
    private OrderItemDailyFood orderItemDailyFood;
    private Integer count;
    private BigDecimal supportPrice;

    public DailySupportPriceDto(OrderItemDailyFood orderItemDailyFood, Integer count, BigDecimal supportPrice) {
        this.orderItemDailyFood = orderItemDailyFood;
        this.count = count;
        this.supportPrice = supportPrice;
    }
}
