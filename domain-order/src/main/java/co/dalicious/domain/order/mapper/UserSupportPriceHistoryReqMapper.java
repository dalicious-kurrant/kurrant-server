package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.system.util.PriceUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = PriceUtils.class)
public interface UserSupportPriceHistoryReqMapper {
    @Mapping(source = "orderItemDailyFood.order.user", target = "user")
    @Mapping(source = "orderItemDailyFood.order", target = "group", qualifiedByName = "getGroup")
    @Mapping(source = "supportPrice", target = "usingSupportPrice")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.serviceDate", target = "serviceDate")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    @Mapping(target = "monetaryStatus", constant = "DEDUCTION")
    UserSupportPriceHistory toEntity(OrderItemDailyFood orderItemDailyFood, BigDecimal supportPrice);

    @Mapping(source = "order.user", target = "user")
    @Mapping(source = "order", target = "group", qualifiedByName = "getGroup")
    @Mapping(target = "usingSupportPrice", expression = "java(PriceUtils.roundToOneDigit(orderItemDailyFood.getOrderItemTotalPrice().multiply(BigDecimal.valueOf(0.5))))")
    @Mapping(source = "orderItemDailyFoodGroup.serviceDate", target = "serviceDate")
    @Mapping(source = "orderItemDailyFoodGroup.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    @Mapping(target = "monetaryStatus", constant = "DEDUCTION")
    UserSupportPriceHistory toMedTronicSupportPrice(OrderItemDailyFood orderItemDailyFood);

    @Named("getGroup")
    default Group getGroup(Order order) {
        order = (Order) Hibernate.unproxy(order);
        if(order instanceof OrderDailyFood) {
            return ((OrderDailyFood) order).getSpot().getGroup();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }
}
