package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.order.dto.OrderDailyFoodDetailDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", imports = {DateUtils.class, BigDecimal.class, DailyFoodStatus.class})
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
    @Mapping(source = "orderDailyFood", target = "discountPrice", qualifiedByName = "getDiscountedPrice")
    @Mapping(source = "orderItems", target = "orderItems")
    @Mapping(source = "orderDailyFood.receiptUrl", target = "receiptUrl")
    @Mapping(source = "orderDailyFood.paymentCompany.paymentCompany", target = "paymentCompany")
    @Mapping(source = "refundDto", target = "refundDto")
    OrderDailyFoodDetailDto orderToDto(OrderDailyFood orderDailyFood, List<OrderDailyFoodDetailDto.OrderItem> orderItems, OrderDailyFoodDetailDto.RefundDto refundDto);

    @Mapping(source = "id", target = "id")
    @Mapping(target = "image", expression = "java(orderItemDailyFood.getDailyFood().getFood().getImages() == null ? null : orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation())")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate()))")
    @Mapping(source = "orderItemDailyFoodGroup.diningType.code", target = "diningType")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "name", target = "foodName")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "orderStatus.code", target = "orderStatus")
    @Mapping(target = "price", expression = "java(orderItemDailyFood.getDiscountedPrice().multiply(BigDecimal.valueOf(orderItemDailyFood.getCount())))")
    @Mapping(target = "isBeforeLastOrderTime", expression = "java(!orderItemDailyFood.getDailyFood().getDailyFoodStatus().equals(DailyFoodStatus.PASS_LAST_ORDER_TIME))")
    OrderDailyFoodDetailDto.OrderItem orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    @Named("getSupportPrice")
    default BigDecimal getSupportPrice(OrderDailyFood orderDailyFood) {
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        BigDecimal supportPrice = BigDecimal.ZERO;
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new HashSet<>();

        for (OrderItem orderItem : orderItems) {
            orderItemDailyFoodGroups.add(((OrderItemDailyFood) orderItem).getOrderItemDailyFoodGroup());
        }

        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroups) {
            for (UserSupportPriceHistory userSupportPriceHistory : orderItemDailyFoodGroup.getUserSupportPriceHistories()) {
                if (userSupportPriceHistory.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                    supportPrice = supportPrice.add(userSupportPriceHistory.getUsingSupportPrice());
                }
            }
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

    @Named("getDiscountedPrice")
    default BigDecimal getDiscountedPrice(OrderDailyFood orderDailyFood) {
        BigDecimal totalDiscountedPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = orderDailyFood.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            totalDiscountedPrice = totalDiscountedPrice.add(((OrderItemDailyFood) orderItem).getMembershipDiscountPrice());
            totalDiscountedPrice = totalDiscountedPrice.add(((OrderItemDailyFood) orderItem).getMakersDiscountPrice());
            totalDiscountedPrice = totalDiscountedPrice.add(((OrderItemDailyFood) orderItem).getPeriodDiscountPrice());
        }
        return totalDiscountedPrice;
    }
}
