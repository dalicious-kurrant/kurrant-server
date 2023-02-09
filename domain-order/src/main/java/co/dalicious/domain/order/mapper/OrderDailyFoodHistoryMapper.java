package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderHistoryDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFoodGroup;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, BigDecimal.class})
public interface OrderDailyFoodHistoryMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "dailyFood.food.makers.name", target = "makersName")
    @Mapping(source = "dailyFood.food.name", target = "name")
    @Mapping(source = "dailyFood.food.image.location", target = "image")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate()))")
    @Mapping(source = "orderItemDailyFoodGroup.diningType.code", target = "diningType")
    @Mapping(source = "count", target = "count")
    @Mapping(target = "price", expression = "java(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())))")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    OrderHistoryDto.OrderItem orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.orderType.code", target = "orderType")
    @Mapping(source = "order.code", target = "code")
    @Mapping(target = "orderDate", expression = "java(DateUtils.toISOLocalDate(order.getCreatedDateTime()))")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderHistoryDto orderToDto(Order order, List<OrderHistoryDto.OrderItem> orderItems);
}
