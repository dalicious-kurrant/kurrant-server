package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderDailyFoodDetailDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface OrderDailyFoodDetailMapper {
    @Mapping(source = "orderDailyFood.code", target = "code")
    @Mapping(target = "orderDate", expression = "java(DateUtils.toISOLocalDate(orderDailyFood.getCreatedDateTime()))")
    @Mapping(source = "orderDailyFood.orderType.orderType", target = "orderType")
    @Mapping(source = "orderDailyFood.user.name", target = "userName")
    @Mapping(source = "orderDailyFood.groupName", target = "groupName")
    @Mapping(source = "orderDailyFood.spotName", target = "spotName")
    @Mapping(source = "orderDailyFood.ho", target = "ho")
    @Mapping(target = "address", expression = "java(orderDailyFood.getAddress().addressToString())")
    @Mapping(source = "orderDailyFood.defaultPrice", target = "defaultPrice")
    @Mapping(source = "orderDailyFood", target = "supportPrice", qualifiedByName = "getSupportPrice")
    @Mapping(source = "orderDailyFood", target = "membershipDiscountPrice", qualifiedByName = "getMembershipDiscountPrice")
    @Mapping(source = "orderDailyFood", target = "makersDiscountPrice", qualifiedByName = "getMakersDiscountPrice")
    @Mapping(source = "orderDailyFood", target = "periodDiscountPrice", qualifiedByName = "getPeriodDiscountPrice")
    @Mapping(source = "orderDailyFood.totalDeliveryFee", target = "deliveryFee")
    @Mapping(source = "orderDailyFood.point", target = "point")
    @Mapping(source = "orderDailyFood.totalPrice", target = "totalPrice")
    @Mapping(target = "discountPrice", expression = "java(orderDailyFood.getDefaultPrice().subtract(orderDailyFood.getTotalPrice()))")
    @Mapping(target = "paymentInfo", expression = "java(getPaymentInfo())")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDailyFoodDetailDto orderToDto(OrderDailyFood orderDailyFood, List<OrderDailyFoodDetailDto.OrderItem> orderItems);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "food.image.location", target = "image")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getServiceDate()))")
    @Mapping(source = "diningType.code", target = "diningType")
    @Mapping(source = "food.makers.name", target = "makers")
    @Mapping(source = "name", target = "foodName")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    OrderDailyFoodDetailDto.OrderItem orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    // TODO: 상진님과 의논 필요 -> Order에 카드정보를 저장할 지.
    @Named("getPaymentInfo")
    default String getPaymentInfo() {
        return "신한(1234)";
    }

    @Named("getSupportPrice")
    default BigDecimal getSupportPrice(OrderDailyFood orderDailyFood) {
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        BigDecimal supportPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            supportPrice = supportPrice.add((orderItemDailyFood.getUserSupportPriceHistory() == null) ? BigDecimal.ZERO : orderItemDailyFood.getUserSupportPriceHistory().getUsingSupportPrice());
        }
        return supportPrice;
    }

    @Named("getMembershipDiscountPrice")
    default BigDecimal getMembershipDiscountPrice(OrderDailyFood orderDailyFood) {
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        BigDecimal membershipTotalDiscountPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            membershipTotalDiscountPrice = membershipTotalDiscountPrice.add(orderItemDailyFood.getMembershipDiscountPrice());
        }
        return membershipTotalDiscountPrice;
    }

    @Named("getMakersDiscountPrice")
    default BigDecimal getMakersDiscountPrice(OrderDailyFood orderDailyFood) {
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        BigDecimal makersTotalDiscountPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            makersTotalDiscountPrice = makersTotalDiscountPrice.add(orderItemDailyFood.getMakersDiscountPrice());
        }
        return makersTotalDiscountPrice;
    }

    @Named("getPeriodDiscountPrice")
    default BigDecimal getPeriodDiscountPrice(OrderDailyFood orderDailyFood) {
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        BigDecimal getPeriodDiscountPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            getPeriodDiscountPrice = getPeriodDiscountPrice.add(orderItemDailyFood.getPeriodDiscountPrice());
        }
        return getPeriodDiscountPrice;
    }
}
