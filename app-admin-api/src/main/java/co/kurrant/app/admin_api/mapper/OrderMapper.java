package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.OrderDto;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface OrderMapper {
    @Mapping(source = "id", target = "orderItemDailyFoodId")
    @Mapping(target = "serviceDate", expression = "java(DateUtils.format(orderItemDailyFood.getDailyFood().getServiceDate()))")
    @Mapping(source = "dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(source = "discountedPrice", target = "price")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "order.user.name", target = "userName")
    @Mapping(source = "order.user.phone", target = "phone")
    @Mapping(source = "order.code", target = "orderCode")
    OrderDto.OrderItemDailyFood orderItemDailyFoodToDto(OrderItemDailyFood orderItemDailyFood);

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
//        List<OrderDto.OrderItemDailyFoodList> orderItemDailyFoodList = new ArrayList<>();
//        MultiValueMap<Spot, OrderItemDailyFood> spotMap = new LinkedMultiValueMap<>();
//        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
//            OrderDailyFood orderDailyFood = (OrderDailyFood) orderItemDailyFood.getOrder();
//            spotMap.add(orderDailyFood.getSpot(), orderItemDailyFood);
//        }
//        for (Spot spot : spotMap.keySet()) {
//            MultiValueMap<DiningType, OrderItemDailyFood> diningTypeMap = new LinkedMultiValueMap<>();
//            List<OrderItemDailyFood> spotOrderItemDailyFoods = spotMap.getOrDefault(spot, null);
//            for (OrderItemDailyFood spotOrderItemDailyFood : spotOrderItemDailyFoods) {
//                diningTypeMap.add(spotOrderItemDailyFood.getDailyFood().getDiningType(), spotOrderItemDailyFood);
//            }
//            for (DiningType diningType : diningTypeMap.keySet()) {
//                orderItemDailyFoodList.add(toOrderItemDailyFoodListDto(spot, diningType, diningTypeMap.get(diningType)));
//            }
//        }
//        return orderItemDailyFoodList;
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

        //        List<OrderDto.SpotFoodMap> spotFoodMaps = new ArrayList<>();
//        Map<Food, Integer> foodMap = new HashMap<>();
//        Food food = null;
//        Integer count = 0;
//        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
//            food = orderItemDailyFood.getDailyFood().getFood();
//            count = orderItemDailyFood.getCount();
//            if(foodMap.getOrDefault(food, null) == null) {
//                foodMap.put(food, count);
//            } else {
//                foodMap.put(food, foodMap.get(food) + count);
//            }
//        }
//        for (Food filterdFood : foodMap.keySet()) {
//            OrderDto.SpotFoodMap spotFood = new OrderDto.SpotFoodMap();
//            spotFood.setFoodName(filterdFood.getName());
//            spotFood.setCount(foodMap.get(filterdFood));
//        }
//        return spotFoodMaps;
    }
}

