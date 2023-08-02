package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderHistoryDto;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, BigDecimal.class})
public interface OrderDailyFoodHistoryMapper {
    @Mapping(source = "dailyFood.food.makers.name", target = "makersName")
    @Mapping(target = "image", expression = "java(orderItemDailyFood.getDailyFood().getFood().getImages() == null || orderItemDailyFood.getDailyFood().getFood().getImages().isEmpty() ? null : orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation())")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate()))")
    @Mapping(source = "orderItemDailyFoodGroup.diningType.code", target = "diningType")
    @Mapping(target = "price", expression = "java(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())))")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    @Mapping(source = "dailyFood.dailyFoodStatus.code", target = "dailyFoodStatus")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.timeToString(orderItemDailyFood.getDeliveryTime()))")
    @Mapping(target = "lastOrderTime", expression = "java(orderItemDailyFood.getLastOrderTime())")
    OrderHistoryDto.OrderItem orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.orderType.code", target = "orderType")
    @Mapping(source = "order.code", target = "code")
    @Mapping(target = "orderDate", expression = "java(DateUtils.toISOLocalDate(order.getCreatedDateTime()))")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderHistoryDto orderToDto(Order order, List<OrderHistoryDto.OrderItem> orderItems);
}
