package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.dto.DiningTypeServiceDateDto;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDailyFoodByMakersMapper {
    default OrderDailyFoodByMakersDto.ByPeriod toDto(List<OrderItemDailyFood> orderItemDailyFoodList) {
        OrderDailyFoodByMakersDto.ByPeriod byPeriod = new OrderDailyFoodByMakersDto.ByPeriod();
        MultiValueMap<DiningTypeServiceDateDto, OrderItemDailyFood> diningTypeServiceDateMap = new LinkedMultiValueMap<>();
        MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
            DiningTypeServiceDateDto diningTypeServiceDateDto = new DiningTypeServiceDateDto(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate(), orderItemDailyFood.getOrderItemDailyFoodGroup().getDiningType());
            diningTypeServiceDateMap.add(diningTypeServiceDateDto, orderItemDailyFood);
            foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
        }
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = toFoodByDateDiningType(diningTypeServiceDateMap);
        List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> foodByGroupPeriods = toGroupFoodByGroupPeriod(diningTypeServiceDateMap);
        List<OrderDailyFoodByMakersDto.Foods> foods = toFoods(foodMap);

        byPeriod.setFoodByDateDiningTypes(foodByDateDiningTypes);
        byPeriod.setGroupFoodByDateDiningTypes(foodByGroupPeriods);
        byPeriod.setTotalFoods(foods);

        return byPeriod;
    }

    default List<OrderDailyFoodByMakersDto.FoodByDateDiningType> toFoodByDateDiningType(MultiValueMap<DiningTypeServiceDateDto, OrderItemDailyFood> multiValueMap) {
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = new ArrayList<>();
        for (DiningTypeServiceDateDto diningTypeServiceDateDto : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(diningTypeServiceDateDto);

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
            foodByDateDiningType.setServiceDate(DateUtils.format(diningTypeServiceDateDto.getServiceDate()));
            foodByDateDiningType.setDiningType(diningTypeServiceDateDto.getDiningType().getDiningType());
            foodByDateDiningType.setTotalCount(totalCount);
            foodByDateDiningType.setFoods(foodList);

            foodByDateDiningTypes.add(foodByDateDiningType);
        }
        return foodByDateDiningTypes;
    }

    default List<OrderDailyFoodByMakersDto.Food> toDto(MultiValueMap<Food, OrderItemDailyFood> foodMultiValueMap) {
        List<OrderDailyFoodByMakersDto.Food> foodDtoList = new ArrayList<>();
        for (Food food : foodMultiValueMap.keySet()) {
            OrderDailyFoodByMakersDto.Food foodDto = new OrderDailyFoodByMakersDto.Food();
            foodDto.setFoodId(food.getId());
            Integer count = 0;
            for (OrderItemDailyFood orderItemDailyFood : foodMultiValueMap.get(food)) {
                count += orderItemDailyFood.getCount();
            }
            foodDto.setFoodCount(count);
            foodDtoList.add(foodDto);
        }
        return foodDtoList;
    }

    default List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> toGroupFoodByGroupPeriod(MultiValueMap<DiningTypeServiceDateDto, OrderItemDailyFood> multiValueMap) {
        List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> groupFoodByDateDiningTypes = new ArrayList<>();

        for (DiningTypeServiceDateDto diningTypeServiceDateDto : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(diningTypeServiceDateDto);

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
            groupFoodByDateDiningType.setServiceDate(DateUtils.format(diningTypeServiceDateDto.getServiceDate()));
            groupFoodByDateDiningType.setDiningType(diningTypeServiceDateDto.getDiningType().getDiningType());
            groupFoodByDateDiningType.setFoodByGroups(foodByGroups);

            groupFoodByDateDiningTypes.add(groupFoodByDateDiningType);
        }
        return groupFoodByDateDiningTypes;
    }

    default List<OrderDailyFoodByMakersDto.SpotByDateDiningType> toSpotByDateDiningType(MultiValueMap<Spot, OrderItemDailyFood> spotMap) {
        List<OrderDailyFoodByMakersDto.SpotByDateDiningType> spotByDateDiningTypes = new ArrayList<>();

        MultiValueMap<Food, OrderItemDailyFood> foodMultiValueMap = new LinkedMultiValueMap<>();
        for (Spot spot : spotMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = spotMap.get(spot);
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                Food food = orderItemDailyFood.getDailyFood().getFood();
                foodMultiValueMap.add(food, orderItemDailyFood);
            }
            List<OrderDailyFoodByMakersDto.Food> foodList = new ArrayList<>();
            for (Food food : foodMultiValueMap.keySet()) {
                List<OrderItemDailyFood> foodOrderItemDailyFood = foodMultiValueMap.get(food);
                Integer count = 0;
                for (OrderItemDailyFood orderItemDailyFood : foodOrderItemDailyFood) {
                    count += orderItemDailyFood.getCount();
                }
                OrderDailyFoodByMakersDto.Food foodDto = new OrderDailyFoodByMakersDto.Food();
                foodDto.setFoodId(food.getId());
                foodDto.setFoodCount(count);
                foodList.add(foodDto);
            }
            OrderDailyFoodByMakersDto.SpotByDateDiningType spotByDateDiningType = new OrderDailyFoodByMakersDto.SpotByDateDiningType();
            spotByDateDiningType.setSpotId(spot.getId());
            spotByDateDiningType.setSpotName(spot.getName());
            spotByDateDiningType.setFoods(foodList);
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
            foods.setFoodName(food.getName());
            foods.setTotalFoodCount(count);
            foodsList.add(foods);
        }
        return foodsList;
    }
}