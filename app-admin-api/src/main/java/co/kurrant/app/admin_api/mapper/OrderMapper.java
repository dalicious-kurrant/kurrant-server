package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFoodGroup;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.OrderDto;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, OrderDailyFood.class, Hibernate.class, OrderUtil.class, UserSupportPriceUtil.class})
public interface OrderMapper {
    @Mapping(source = "id", target = "orderItemDailyFoodId")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getDailyFood().getServiceDate()))")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(target = "price", expression = "java(orderItemDailyFood.getOrderItemTotalPrice())")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "order.user.name", target = "userName")
    @Mapping(source = "order.user.phone", target = "phone")
    @Mapping(source = "order.code", target = "orderCode")
    @Mapping(source = "dailyFood.group.name", target = "groupName")
    @Mapping(target = "spotName", expression = "java(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpotName())")
    @Mapping(source = "dailyFood.diningType.diningType", target = "diningType")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.timeToString(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot().getMealInfo(orderItemDailyFood.getDailyFood().getDiningType()).getDeliveryTime()))")
    OrderDto.OrderItemDailyFood orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

    @Mapping(source = "id", target = "orderItemDailyFoodId")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(target = "discountedPrice", expression = "java(orderItemDailyFood.getOrderItemTotalPrice())")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "orderStatus.orderStatus", target = "orderStatus")
    OrderDto.OrderItemDailyFoodGroupItem orderItemDailyFoodGroupItemToDto(OrderItemDailyFood orderItemDailyFood);

    default List<OrderDto.OrderItemDailyFoodList> ToDtoByGroup(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<Spot, OrderItemDailyFood> spotMap = new LinkedMultiValueMap<>();
        orderItemDailyFoods.forEach(orderItemDailyFood -> {
            OrderDailyFood orderDailyFood = (OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder());
            spotMap.add(orderDailyFood.getSpot(), orderItemDailyFood);
        });

        return spotMap.entrySet().stream()
                .flatMap(spotEntry -> {
                    MultiValueMap<DiningType, OrderItemDailyFood> diningTypeMap = new LinkedMultiValueMap<>();
                    spotEntry.getValue().forEach(spotOrderItemDailyFood -> {
                        diningTypeMap.add(spotOrderItemDailyFood.getDailyFood().getDiningType(), spotOrderItemDailyFood);
                    });
                    return diningTypeMap.entrySet().stream()
                            .map(diningTypeEntry -> toOrderItemDailyFoodListDto(spotEntry.getKey(), diningTypeEntry.getKey(), diningTypeEntry.getValue()));
                })
                .collect(Collectors.toList());
    };

    default OrderDto.OrderItemDailyFoodList toOrderItemDailyFoodListDto(Spot spot, DiningType diningType, List<OrderItemDailyFood> orderItemDailyFoods) {
        OrderDto.OrderItemDailyFoodList orderItemDailyFoodList = new OrderDto.OrderItemDailyFoodList();
        orderItemDailyFoodList.setSpotId(spot.getId());
        orderItemDailyFoodList.setSpotFoodCount(getFoodCount(orderItemDailyFoods));
        orderItemDailyFoodList.setSpotName(spot.getName());
        orderItemDailyFoodList.setDiningType(diningType.getDiningType());
        orderItemDailyFoodList.setFoodMap(toFoodMap(orderItemDailyFoods));
        orderItemDailyFoodList.setOrderItemDailyFoods(orderItemDailyFoodsToDtos(orderItemDailyFoods));
        return orderItemDailyFoodList;
    }

    default Integer getFoodCount(List<OrderItemDailyFood> orderItemDailyFoods) {
        Integer count = 0;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            count += orderItemDailyFood.getCount();
        }
        return count;
    }

    default List<OrderDto.OrderItemDailyFood> orderItemDailyFoodsToDtos(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .map(this::orderItemDailyFoodToDto)
                .collect(Collectors.toList());
    }

    default List<OrderDto.SpotFoodMap> toFoodMap(List<OrderItemDailyFood> orderItemDailyFoods) {
        Map<Food, Integer> foodMap = orderItemDailyFoods.stream()
                .collect(Collectors.groupingBy(v -> v.getDailyFood().getFood(), Collectors.summingInt(OrderItemDailyFood::getCount)));

        return foodMap.entrySet().stream()
                .map(entry -> {
                    OrderDto.SpotFoodMap spotFood = new OrderDto.SpotFoodMap();
                    spotFood.setFoodName(entry.getKey().getName());
                    spotFood.setCount(entry.getValue());
                    return spotFood;
                })
                .collect(Collectors.toList());
    }

    default OrderDto.OrderDailyFoodDetail orderToDetailDto(OrderDailyFood orderDailyFood) {
        OrderDto.OrderDailyFoodDetail orderDailyFoodDetail = new OrderDto.OrderDailyFoodDetail();
        List<OrderDto.OrderItemDailyFoodGroup> orderItemDailyFoodGroups = toOrderItemDailyFoodGroupDto(orderDailyFood.getOrderItems());

        LocalDate startDate = orderItemDailyFoodGroups.stream()
                .map(orderItemDailyFoodGroup -> DateUtils.stringToDate(orderItemDailyFoodGroup.getServiceDate()))
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        LocalDate endDate = orderItemDailyFoodGroups.stream()
                .map(orderItemDailyFoodGroup -> DateUtils.stringToDate(orderItemDailyFoodGroup.getServiceDate()))
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        BigDecimal totalSupportPrice = orderItemDailyFoodGroups.stream()
                .map(OrderDto.OrderItemDailyFoodGroup::getSupportPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        orderDailyFoodDetail.setOrderId(orderDailyFood.getId());
        orderDailyFoodDetail.setOrderCode(orderDailyFood.getCode());
        orderDailyFoodDetail.setUserName(orderDailyFood.getUser().getName());
        orderDailyFoodDetail.setServicePeriod(DateUtils.format(startDate) + " ~ " + DateUtils.format(endDate));
        orderDailyFoodDetail.setSpotName(orderDailyFood.getSpotName());
        orderDailyFoodDetail.setTotalPrice(orderDailyFood.getTotalPrice());
        orderDailyFoodDetail.setUsingSupportPrice(totalSupportPrice);
        orderDailyFoodDetail.setDeliveryFee(orderDailyFood.getTotalDeliveryFee());
        orderDailyFoodDetail.setPoint(orderDailyFood.getPoint());
        orderDailyFoodDetail.setOrderItemDailyFoodGroups(orderItemDailyFoodGroups);

        return orderDailyFoodDetail;
    }

    default List<OrderDto.OrderItemDailyFoodGroup> toOrderItemDailyFoodGroupDto(List<OrderItem> orderItems) {
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroupSet = new HashSet<>();
        List<OrderDto.OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
                orderItemDailyFoodGroupSet.add(orderItemDailyFood.getOrderItemDailyFoodGroup());
            }
        }
        for (OrderItemDailyFoodGroup orderItemDailyFoodGroup : orderItemDailyFoodGroupSet) {
            OrderDto.OrderItemDailyFoodGroup orderItemDailyFoodGroupDto = new OrderDto.OrderItemDailyFoodGroup();
            orderItemDailyFoodGroupDto.setServiceDate(DateUtils.format(orderItemDailyFoodGroup.getServiceDate()));
            orderItemDailyFoodGroupDto.setDiningType(orderItemDailyFoodGroup.getDiningType().getDiningType());
            orderItemDailyFoodGroupDto.setTotalPrice(orderItemDailyFoodGroup.getTotalPriceByGroup());
            orderItemDailyFoodGroupDto.setSupportPrice(orderItemDailyFoodGroup.getUsingSupportPrice());
            orderItemDailyFoodGroupDto.setPayPrice(OrderUtil.getPaidPriceGroupByOrderItemDailyFoodGroup(orderItemDailyFoodGroup));
            orderItemDailyFoodGroupDto.setDeliveryPrice((orderItemDailyFoodGroup.getOrderStatus() != OrderStatus.CANCELED) ? orderItemDailyFoodGroup.getDeliveryFee() : BigDecimal.ZERO);
            orderItemDailyFoodGroupDto.setOrderItemDailyFoods(orderItemDailyFoodGroupItemsToDtos(orderItemDailyFoodGroup.getOrderDailyFoods()));

            orderItemDailyFoodGroups.add(orderItemDailyFoodGroupDto);
        }
        return orderItemDailyFoodGroups;
    }

    default List<OrderDto.OrderItemDailyFoodGroupItem> orderItemDailyFoodGroupItemsToDtos(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .map(this::orderItemDailyFoodGroupItemToDto)
                .collect(Collectors.toList());
    }
}

