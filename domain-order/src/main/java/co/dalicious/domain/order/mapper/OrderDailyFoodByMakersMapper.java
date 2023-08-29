package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.food.util.FoodUtils;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.dto.ServiceDiningVo;
import co.dalicious.domain.order.dto.SpotDeliveryTimeDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, FoodUtils.class})
public interface OrderDailyFoodByMakersMapper {
    default OrderDailyFoodByMakersDto.ByPeriod toDto(List<OrderItemDailyFood> orderItemDailyFoodList, List<FoodCapacity> foodCapacities) {
        OrderDailyFoodByMakersDto.ByPeriod byPeriod = new OrderDailyFoodByMakersDto.ByPeriod();
        MultiValueMap<ServiceDiningVo, OrderItemDailyFood> diningTypeServiceDateMap = new LinkedMultiValueMap<>();
        MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
            ServiceDiningVo serviceDiningVo = new ServiceDiningVo(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate(), orderItemDailyFood.getOrderItemDailyFoodGroup().getDiningType());
            diningTypeServiceDateMap.add(serviceDiningVo, orderItemDailyFood);
            foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
        }
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = toFoodByDateDiningType(diningTypeServiceDateMap, foodCapacities);
        List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> deliveryGroupsByDates = toDeliveryGroupsByDate(diningTypeServiceDateMap, foodCapacities);
        List<OrderDailyFoodByMakersDto.Foods> foods = toFoods(foodMap);

        byPeriod.setFoodByDateDiningTypes(foodByDateDiningTypes);
        byPeriod.setTotalFoods(foods);
        byPeriod.setDeliveryGroupsByDates(deliveryGroupsByDates);

        return byPeriod;
    }

    default List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> toDeliveryGroupsByDate(MultiValueMap<ServiceDiningVo, OrderItemDailyFood> diningTypeServiceDateMap, List<FoodCapacity> foodCapacities) {
        List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> deliveryGroupsByDates = new ArrayList<>();
        for (ServiceDiningVo serviceDiningVo : diningTypeServiceDateMap.keySet()) {
            OrderDailyFoodByMakersDto.DeliveryGroupsByDate deliveryGroupsByDate = new OrderDailyFoodByMakersDto.DeliveryGroupsByDate();
            List<OrderItemDailyFood> orderItemDailyFoods = diningTypeServiceDateMap.get(serviceDiningVo);
            deliveryGroupsByDate.setServiceDate(DateUtils.format(serviceDiningVo.getServiceDate()));
            deliveryGroupsByDate.setDiningType(serviceDiningVo.getDiningType().getDiningType());
            deliveryGroupsByDate.setDeliveryGroups(toDeliveryGroups(orderItemDailyFoods));
            deliveryGroupsByDate.setSpotCount(deliveryGroupsByDate.getSpotCount());

            LocalDateTime lastOrderTime = FoodUtils.getLastOrderTime(foodCapacities.get(0).getFood().getMakers(), serviceDiningVo.getDiningType(), serviceDiningVo.getServiceDate(), foodCapacities);
            deliveryGroupsByDate.setLastOrderTime(DateUtils.localDateTimeToString(lastOrderTime));
            if(lastOrderTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Seoul")))) deliveryGroupsByDate.setBeforeLastOrderTime(false);
            deliveryGroupsByDate.setBeforeLastOrderTime(true);

            deliveryGroupsByDates.add(deliveryGroupsByDate);
        }
        deliveryGroupsByDates = deliveryGroupsByDates.stream()
                .sorted(Comparator.comparing((OrderDailyFoodByMakersDto.DeliveryGroupsByDate v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        return deliveryGroupsByDates;
    }

    default List<OrderDailyFoodByMakersDto.DeliveryGroups> toDeliveryGroups(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<LocalTime, OrderItemDailyFood> itemsByTime = new LinkedMultiValueMap<>();
        List<OrderDailyFoodByMakersDto.DeliveryGroups> deliveryGroupsList = new ArrayList<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            List<DeliverySchedule> deliverySchedules = orderItemDailyFood.getDailyFood().getDailyFoodGroup().getDeliverySchedules();
            LocalTime pickupTime = deliverySchedules.stream()
                    .filter(v -> v.getDeliveryTime().equals(orderItemDailyFood.getDeliveryTime()))
                    .findAny()
                    .map(DeliverySchedule::getPickupTime)
                    .orElse(null);
            itemsByTime.add(pickupTime, orderItemDailyFood);
        }
        for (LocalTime localTime : itemsByTime.keySet()) {
            OrderDailyFoodByMakersDto.DeliveryGroups deliveryGroups = new OrderDailyFoodByMakersDto.DeliveryGroups();
            // FIXME: OrderItemDailyFood에 LocalTime이 없을 경우?
            List<OrderDailyFoodByMakersDto.FoodBySpot> foodBySpots = toFoodBySpot(itemsByTime.get(localTime));
            deliveryGroups.setPickUpTime(localTime == null ? null : DateUtils.timeToString(localTime));
            deliveryGroups.setFoods(toFoods(itemsByTime.get(localTime)));
            deliveryGroups.setFoodCount(deliveryGroups.getFoodCount());
            deliveryGroups.setFoodBySpots(foodBySpots);
            deliveryGroups.setSpotCount(deliveryGroups.getSpotCount());
            deliveryGroupsList.add(deliveryGroups);
        }
        deliveryGroupsList = deliveryGroupsList.stream()
                .sorted(Comparator.comparing(v -> (v.getPickUpTime() != null ? LocalTime.parse(v.getPickUpTime()) : LocalTime.MIN), Comparator.nullsLast(LocalTime::compareTo)))
                .toList();
        return deliveryGroupsList;
    }

    default List<OrderDailyFoodByMakersDto.Food> toFoods(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();
        List<OrderDailyFoodByMakersDto.Food> foodDtoList = new ArrayList<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
        }

        for (Food food : foodMap.keySet()) {
            OrderDailyFoodByMakersDto.Food foodDto = new OrderDailyFoodByMakersDto.Food();
            Integer count = 0;
            for (OrderItemDailyFood orderItemDailyFood : foodMap.get(food)) {
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

    default List<OrderDailyFoodByMakersDto.FoodBySpot> toFoodBySpot(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<SpotDeliveryTimeDto, OrderItemDailyFood> spotMap = new LinkedMultiValueMap<>();
        List<OrderDailyFoodByMakersDto.FoodBySpot> foodBySpots = new ArrayList<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            SpotDeliveryTimeDto spotDeliveryTimeDto = new SpotDeliveryTimeDto(((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot(), orderItemDailyFood.getDeliveryTime());
            spotMap.add(spotDeliveryTimeDto, orderItemDailyFood);
        }
        for (SpotDeliveryTimeDto spot : spotMap.keySet()) {
            OrderDailyFoodByMakersDto.FoodBySpot foodBySpot = new OrderDailyFoodByMakersDto.FoodBySpot();
            //FIXME: 배송 ID 설정
            foodBySpot.setDeliveryId(spot.getSpot().getId().toString());
            foodBySpot.setSpotType(GroupDataType.ofClass(Hibernate.getClass(spot.getSpot())).getCode());
            foodBySpot.setDeliveryTime(DateUtils.timeToString(spot.getDeliveryTime()));
            foodBySpot.setAddress1(spot.getSpot().getAddress().addressToString());
            foodBySpot.setAddress2(spot.getSpot().getAddress().getAddress2()); //수정 필요
            foodBySpot.setSpotName(spot.getSpot().getName());
            foodBySpot.setGroupName(spot.getSpot() instanceof MySpot ? null : spot.getSpot().getGroup().getName());
            foodBySpot.setUserName(Hibernate.getClass(spot.getSpot()) == CorporationSpot.class ? null : spotMap.get(spot).get(0).getOrder().getUser().getName());
            foodBySpot.setPhone(Hibernate.getClass(spot.getSpot()) == CorporationSpot.class ? null : spotMap.get(spot).get(0).getOrder().getUser().getPhone());
            foodBySpot.setFoods(toFoods(spotMap.get(spot)));
            foodBySpot.setFoodCount(foodBySpot.getFoodCount());
            foodBySpots.add(foodBySpot);
        }
        foodBySpots = foodBySpots.stream()
                .sorted(Comparator.comparing(
                        OrderDailyFoodByMakersDto.FoodBySpot::getDeliveryTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());
        return foodBySpots;
    }

    default List<OrderDailyFoodByMakersDto.FoodByDateDiningType> toFoodByDateDiningType(MultiValueMap<ServiceDiningVo, OrderItemDailyFood> multiValueMap, List<FoodCapacity> foodCapacities) {
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = new ArrayList<>();
        for (ServiceDiningVo serviceDiningVo : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(serviceDiningVo);

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
            foodByDateDiningType.setServiceDate(DateUtils.format(serviceDiningVo.getServiceDate()));
            foodByDateDiningType.setDiningType(serviceDiningVo.getDiningType().getDiningType());
            foodByDateDiningType.setTotalCount(totalCount);
            foodByDateDiningType.setFoods(foodList);

            LocalDateTime lastOrderTime = FoodUtils.getLastOrderTime(foodCapacities.get(0).getFood().getMakers(), serviceDiningVo.getDiningType(), serviceDiningVo.getServiceDate(), foodCapacities);
            foodByDateDiningType.setLastOrderTime(DateUtils.localDateTimeToString(lastOrderTime));
            if(lastOrderTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Seoul")))) foodByDateDiningType.setBeforeLastOrderTime(false);
            foodByDateDiningType.setBeforeLastOrderTime(true);

            foodByDateDiningTypes.add(foodByDateDiningType);
        }
        foodByDateDiningTypes = foodByDateDiningTypes.stream()
                .sorted(Comparator.comparing((OrderDailyFoodByMakersDto.FoodByDateDiningType v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        return foodByDateDiningTypes;
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


    // FIXME: 삭제
    default List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> toGroupFoodByGroupPeriod(MultiValueMap<ServiceDiningVo, OrderItemDailyFood> multiValueMap) {
        List<OrderDailyFoodByMakersDto.GroupFoodByDateDiningType> groupFoodByDateDiningTypes = new ArrayList<>();

        for (ServiceDiningVo serviceDiningVo : multiValueMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoods = multiValueMap.get(serviceDiningVo);

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
            groupFoodByDateDiningType.setServiceDate(DateUtils.format(serviceDiningVo.getServiceDate()));
            groupFoodByDateDiningType.setDiningType(serviceDiningVo.getDiningType().getDiningType());
            groupFoodByDateDiningType.setFoodByGroups(foodByGroups);

            groupFoodByDateDiningTypes.add(groupFoodByDateDiningType);
        }
        return groupFoodByDateDiningTypes;
    }

    // FIXME: 삭제
    default List<OrderDailyFoodByMakersDto.SpotByDateDiningType> toSpotByDateDiningType(MultiValueMap<Spot, OrderItemDailyFood> spotMap) {
        List<OrderDailyFoodByMakersDto.SpotByDateDiningType> spotByDateDiningTypes = new ArrayList<>();

        DiningType diningType = null;
        for (Spot spot : spotMap.keySet()) {
            MultiValueMap<Food, OrderItemDailyFood> foodMultiValueMap = new LinkedMultiValueMap<>();
            List<OrderItemDailyFood> orderItemDailyFoods = spotMap.get(spot);
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                Food food = orderItemDailyFood.getDailyFood().getFood();
                diningType = orderItemDailyFood.getDailyFood().getDiningType();
                ;
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

            LocalTime deliveryTime = orderItemDailyFoods.get(0).getDeliveryTime();
            LocalTime pickupTime = orderItemDailyFoods.get(0).getDailyFood().getDailyFoodGroup().getDeliverySchedules().stream()
                    .filter(deliverySchedule -> deliverySchedule.getDeliveryTime().equals(deliveryTime))
                    .findAny()
                    .map(DeliverySchedule::getPickupTime)
                    .orElse(LocalTime.parse("00:00"));

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
    // FIXME: 삭제
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
}
