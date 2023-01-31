package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderDailyFoodDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface OrderDailyFoodHistoryMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.name", target = "name")
    @Mapping(source = "food.image.location", target = "image")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getServiceDate(), \"yyyy-MM-dd\"))")
    @Mapping(source = "diningType.code", target = "diningType")
    @Mapping(source = "count", target = "count")
    @Mapping(target = "price", expression = "java(getPayedPrice(orderItemDailyFood))")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    OrderDailyFoodDto.OrderItem orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.orderType.code", target = "orderType")
    @Mapping(source = "order.code", target = "code")
    @Mapping(target = "orderDate", expression = "java(DateUtils.toISOLocalDate(order.getCreatedDateTime()))")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDailyFoodDto orderToDto(Order order, List<OrderDailyFoodDto.OrderItem> orderItems);

    @Named("getPayedPrice")
    default BigDecimal getPayedPrice(OrderItemDailyFood orderItemDailyFood) {
        BigDecimal userSupportPrice = (orderItemDailyFood.getUserSupportPriceHistory() == null) ? BigDecimal.ZERO : orderItemDailyFood.getUserSupportPriceHistory().getUsingSupportPrice();
        return orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())).subtract(userSupportPrice);
    }
}
