package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.system.util.PriceUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = PriceUtils.class)
public interface DailyFoodSupportPriceMapper {
    @Mapping(source = "orderItemDailyFood.order.user", target = "user")
    @Mapping(source = "orderItemDailyFood.order", target = "group", qualifiedByName = "getGroup")
    @Mapping(source = "supportPrice", target = "usingSupportPrice")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.serviceDate", target = "serviceDate")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    @Mapping(target = "monetaryStatus", constant = "DEDUCTION")
    DailyFoodSupportPrice toEntity(OrderItemDailyFood orderItemDailyFood, BigDecimal supportPrice);

    @Mapping(source = "orderItemDailyFood.order.user", target = "user")
    @Mapping(source = "orderItemDailyFood.order", target = "group", qualifiedByName = "getGroup")
    @Mapping(source = "orderItemGroupTotalPrice", target = "usingSupportPrice", qualifiedByName = "getMedTronicPrice")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.serviceDate", target = "serviceDate")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.orderItemDailyFoodGroup", target = "orderItemDailyFoodGroup")
    @Mapping(target = "monetaryStatus", constant = "DEDUCTION")
    DailyFoodSupportPrice toMedTronicSupportPrice(OrderItemDailyFood orderItemDailyFood, BigDecimal orderItemGroupTotalPrice);

    @Named("getGroup")
    default Group getGroup(Order order) {
        order = (Order) Hibernate.unproxy(order);
        if(order instanceof OrderDailyFood) {
            return ((OrderDailyFood) order).getSpot().getGroup();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }

    @Named("getMedTronicPrice")
    default BigDecimal getMedTronicPrice(BigDecimal orderItemGroupTotalPrice) {
        return orderItemGroupTotalPrice.multiply(BigDecimal.valueOf(0.5));
    }
}
