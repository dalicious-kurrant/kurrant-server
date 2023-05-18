package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderDailyFoodByMakersMapper {
    default OrderDailyFoodByMakersDto.ByPeriod toDto(List<OrderItemDailyFood> orderItemDailyFoodList) {
        OrderDailyFoodByMakersDto.ByPeriod byPeriod = new OrderDailyFoodByMakersDto.ByPeriod();
        MultiValueMap<ServiceDiningDto, OrderItemDailyFood> diningTypeServiceDateMap = new LinkedMultiValueMap<>();
        MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate(), orderItemDailyFood.getOrderItemDailyFoodGroup().getDiningType());
            diningTypeServiceDateMap.add(serviceDiningDto, orderItemDailyFood);
            foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
        }
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = toFoodByDateDiningType(diningTypeServiceDateMap);
        List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> foodByGroupPeriods = toGroupFoodByGroupPeriod(diningTypeServiceDateMap);
        foodByGroupPeriods = foodByGroupPeriods.stream()
                .sorted(Comparator.comparing((OrderDailyFoodByMakersDto.GroupFoodByDateDiningType v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        List<OrderDailyFoodByMakersDto.Foods> foods = toFoods(foodMap);

        byPeriod.setFoodByDateDiningTypes(foodByDateDiningTypes);
        byPeriod.setGroupFoodByDateDiningTypes(foodByGroupPeriods);
        byPeriod.setTotalFoods(foods);

        return byPeriod;
    }

    default List<OrderDailyFoodByMakersDto.FoodByDateDiningType> toFoodByDateDiningType(MultiValueMap<ServiceDiningDto, OrderItemDailyFood> multiValueMap) {
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = new ArrayList<>();
        for (ServiceDiningDto serviceDiningDto : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(serviceDiningDto);

            MultiValueMap<Food, OrderItemDailyFood> foodMultiValueMap = new LinkedMultiValueMap<>();
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                foodMultiValueMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
            }
            List<OrderDailyFoodByMakersDto.Food> foodList = toDto(foodMultiValueMap);
            Integer totalCount = 0;
            for (OrderDailyFoodByMakersDto.Food food : foodList) {
                totalCount += food.getFoodCount();
            }

            OrderDailyFoodByMakersDto.FoodByDateDiningType foodByDateDiningType = new OrderDailyFoodByMakersDto.FoodByDateDiningType();
            foodByDateDiningType.setServiceDate(DateUtils.format(serviceDiningDto.getServiceDate()));
            foodByDateDiningType.setDiningType(serviceDiningDto.getDiningType().getDiningType());
            foodByDateDiningType.setTotalCount(totalCount);
            foodByDateDiningType.setFoods(foodList);

            foodByDateDiningTypes.add(foodByDateDiningType);
        }
        foodByDateDiningTypes = foodByDateDiningTypes.stream()
                .sorted(Comparator.comparing((OrderDailyFoodByMakersDto.FoodByDateDiningType v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        return foodByDateDiningTypes;
    }

    default List<OrderDailyFoodByMakersDto.Food> toDto(MultiValueMap<Food, OrderItemDailyFood> foodMultiValueMap) {
        List<OrderDailyFoodByMakersDto.Food> foodDtoList = new ArrayList<>();
        for (Food food : foodMultiValueMap.keySet()) {
            OrderDailyFoodByMakersDto.Food foodDto = new OrderDailyFoodByMakersDto.Food();
            Integer count = 0;
            for (OrderItemDailyFood orderItemDailyFood : foodMultiValueMap.get(food)) {
                count += orderItemDailyFood.getCount();
            }
            foodDto.setFoodId(food.getId());
            foodDto.setFoodCount(count);
            foodDto.setFoodName(food.getName());
            foodDtoList.add(foodDto);
        }
        foodDtoList = foodDtoList.stream()
                .sorted(Comparator.comparing(OrderDailyFoodByMakersDto.Food::getFoodId)).toList();
        return foodDtoList;
    }

    default List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> toGroupFoodByGroupPeriod(MultiValueMap<ServiceDiningDto, OrderItemDailyFood> multiValueMap) {
        List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> groupFoodByDateDiningTypes = new ArrayList<>();

        for (ServiceDiningDto serviceDiningDto : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(serviceDiningDto);

            MultiValueMap<Group, OrderItemDailyFood> groupMap = new LinkedMultiValueMap<>();
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                Group group = orderItemDailyFood.getDailyFood().getGroup();
                groupMap.add(group, orderItemDailyFood);
            }

            List<OrderDailyFoodByMakersDto.FoodByGroup> foodByGroups = new ArrayList<>();

            for (Group group : groupMap.keySet()) {
                MultiValueMap<Spot, OrderItemDailyFood> spotMap = new LinkedMultiValueMap<>();
                List<OrderItemDailyFood> orderItemDailyFoodList = groupMap.get(group);

                assert orderItemDailyFoodList != null;
                for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
                    Spot spot = ((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot();
                    spotMap.add(spot, orderItemDailyFood);
                }

                List<OrderDailyFoodByMakersDto.SpotByDateDiningType> spotByDateDiningTypes = toSpotByDateDiningType(spotMap);

                OrderDailyFoodByMakersDto.FoodByGroup foodByGroup = new OrderDailyFoodByMakersDto.FoodByGroup();
                foodByGroup.setGroupId(group.getId());
                foodByGroup.setGroupName(group.getName());
                foodByGroup.setSpotByDateDiningTypes(spotByDateDiningTypes);

                foodByGroups.add(foodByGroup);
            }

            OrderDailyFoodByMakersDto.GroupFoodByDateDiningType groupFoodByDateDiningType = new OrderDailyFoodByMakersDto.GroupFoodByDateDiningType();
            groupFoodByDateDiningType.setServiceDate(DateUtils.format(serviceDiningDto.getServiceDate()));
            groupFoodByDateDiningType.setDiningType(serviceDiningDto.getDiningType().getDiningType());
            groupFoodByDateDiningType.setFoodByGroups(foodByGroups);

            groupFoodByDateDiningTypes.add(groupFoodByDateDiningType);
        }
        return groupFoodByDateDiningTypes;
    }

    default List<OrderDailyFoodByMakersDto.SpotByDateDiningType> toSpotByDateDiningType(MultiValueMap<Spot, OrderItemDailyFood> spotMap) {
        List<OrderDailyFoodByMakersDto.SpotByDateDiningType> spotByDateDiningTypes = new ArrayList<>();

        DiningType diningType = null;
        for (Spot spot : spotMap.keySet()) {
            MultiValueMap<Food, OrderItemDailyFood> foodMultiValueMap = new LinkedMultiValueMap<>();
            List<OrderItemDailyFood> orderItemDailyFoods = spotMap.get(spot);
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                Food food = orderItemDailyFood.getDailyFood().getFood();
                diningType = orderItemDailyFood.getDailyFood().getDiningType();;
                foodMultiValueMap.add(food, orderItemDailyFood);
            }
            List<OrderDailyFoodByMakersDto.Food> foodList = new ArrayList<>();
            for (Food food : foodMultiValueMap.keySet()) {
                List<OrderItemDailyFood> foodOrderItemDailyFood = foodMultiValueMap.get(food);
                Integer count = 0;
                assert foodOrderItemDailyFood != null;
                for (OrderItemDailyFood orderItemDailyFood : foodOrderItemDailyFood) {
                    count += orderItemDailyFood.getCount();
                }
                OrderDailyFoodByMakersDto.Food foodDto = new OrderDailyFoodByMakersDto.Food();
                foodDto.setFoodId(food.getId());
                foodDto.setFoodCount(count);
                foodDto.setFoodName(food.getName());
                foodList.add(foodDto);
            }
            LocalTime pickupTime = orderItemDailyFoods.get(0).getDailyFood().getDailyFoodGroup().getPickupTime();
            OrderDailyFoodByMakersDto.SpotByDateDiningType spotByDateDiningType = new OrderDailyFoodByMakersDto.SpotByDateDiningType();
            spotByDateDiningType.setSpotId(spot.getId());
            spotByDateDiningType.setSpotName(spot.getName());
            spotByDateDiningType.setFoods(foodList);
            spotByDateDiningType.setPickupTime(DateUtils.timeToString(pickupTime));
            spotByDateDiningType.setDeliveryTime(spot.getDeliveryTime(diningType) == null ? null : spot.getDeliveryTime(diningType).stream().map(DateUtils::timeToString).toList());
            spotByDateDiningTypes.add(spotByDateDiningType);
        }
        return spotByDateDiningTypes;
    }

    default List<OrderDailyFoodByMakersDto.Foods> toFoods(MultiValueMap<Food, OrderItemDailyFood> foodMap) {
        List<OrderDailyFoodByMakersDto.Foods> foodsList = new ArrayList<>();
        for (Food food : foodMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = foodMap.get(food);
            Integer count = 0;
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                count += orderItemDailyFood.getCount();
            }
            OrderDailyFoodByMakersDto.Foods foods = new OrderDailyFoodByMakersDto.Foods();
            foods.setFoodId(food.getId());
            foods.setDescription(food.getDescription());
            foods.setFoodName(food.getName());
            foods.setTotalFoodCount(count);
            foodsList.add(foods);
        }

        foodsList = foodsList.stream()
                .sorted(Comparator.comparing(OrderDailyFoodByMakersDto.Foods::getFoodId)).toList();
        return foodsList;
    }
}
