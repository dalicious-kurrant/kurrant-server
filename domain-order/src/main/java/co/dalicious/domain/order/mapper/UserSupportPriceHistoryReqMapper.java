package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface UserSupportPriceHistoryReqMapper {
    @Mapping(source = "orderItemDailyFood.order.user", target = "user")
    @Mapping(source = "orderItemDailyFood.order", target = "group", qualifiedByName = "getGroup")
    @Mapping(source = "supportPrice", target = "usingSupportPrice")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.serviceDate", target = "serviceDate")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    @Mapping(target = "status", constant = "true")
    UserSupportPriceHistory toEntity(OrderItemDailyFood orderItemDailyFood, BigDecimal supportPrice);

    @Named("getGroup")
    default Group getGroup(Order order) {
        if(order instanceof OrderDailyFood) {
            return ((OrderDailyFood) order).getSpot().getGroup();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }
}
