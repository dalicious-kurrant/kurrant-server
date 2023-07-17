package co.dalicious.domain.delivery.mappper;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.OpenGroupSpot;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.DailyFoodGroup;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeliveryInstanceMapper {
    default DeliveryInstance toEntity(DailyFood dailyFood, Spot spot, Integer orderNumber, LocalTime deliveryTime) {
        return DeliveryInstance.builder()
                .serviceDate(dailyFood.getServiceDate())
                .deliveryTime(deliveryTime)
                .diningType(dailyFood.getDiningType())
                .orderNumber(orderNumber)
                .makers(dailyFood.getFood().getMakers())
                .spot(spot)
                .build();
    }

    default OrderDailyFoodByMakersDto.ByPeriod toDto(List<DeliveryInstance> deliveryInstances) {
        OrderDailyFoodByMakersDto.ByPeriod byPeriod = new OrderDailyFoodByMakersDto.ByPeriod();

        // 1. 메이커스 음식별 개수 및 상세정보
        List<OrderDailyFoodByMakersDto.Foods> foods = toFoods(deliveryInstances);
        byPeriod.setTotalFoods(foods);

        // 2. 메이커스 기간별 음식 개수
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = toFoodByDateDiningType(deliveryInstances);
        byPeriod.setFoodByDateDiningTypes(foodByDateDiningTypes);

        // 3. 고객사별 식사일정
        List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> deliveryGroupsByDates = toDeliveryGroupsByDate(deliveryInstances);
        byPeriod.setDeliveryGroupsByDates(deliveryGroupsByDates);

        return byPeriod;
    }

    default List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> toDeliveryGroupsByDate(List<DeliveryInstance> deliveryInstances) {
        List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> deliveryGroupsByDates = new ArrayList<>();
        MultiValueMap<ServiceDiningDto, DeliveryInstance> deliveryGroupsByDatesMap = new LinkedMultiValueMap<>();
        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(deliveryInstance.getServiceDate(), deliveryInstance.getDiningType());
            deliveryGroupsByDatesMap.add(serviceDiningDto, deliveryInstance);
        }

        for (ServiceDiningDto serviceDiningDto : deliveryGroupsByDatesMap.keySet()) {
            OrderDailyFoodByMakersDto.DeliveryGroupsByDate deliveryGroupsByDate = new OrderDailyFoodByMakersDto.DeliveryGroupsByDate();
            deliveryGroupsByDate.setServiceDate(DateUtils.format(serviceDiningDto.getServiceDate()));
            deliveryGroupsByDate.setDiningType(serviceDiningDto.getDiningType().getDiningType());
            deliveryGroupsByDate.setSpotCount(deliveryGroupsByDatesMap.get(serviceDiningDto).stream().map(DeliveryInstance::getSpot).collect(Collectors.toSet()).size());
            deliveryGroupsByDate.setDeliveryGroups(toDeliveryGroups(deliveryInstances));
            deliveryGroupsByDates.add(deliveryGroupsByDate);
        }

        deliveryGroupsByDates = deliveryGroupsByDates.stream()
                .sorted(Comparator.comparing((OrderDailyFoodByMakersDto.DeliveryGroupsByDate v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        return deliveryGroupsByDates;
    }

    default List<OrderDailyFoodByMakersDto.DeliveryGroups> toDeliveryGroups(List<DeliveryInstance> deliveryInstances) {
        MultiValueMap<LocalTime, DeliveryInstance> itemsByTime = new LinkedMultiValueMap<>();
        List<OrderDailyFoodByMakersDto.DeliveryGroups> deliveryGroupsList = new ArrayList<>();
        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            itemsByTime.add(deliveryInstance.getPickupTime(deliveryInstance.getDeliveryTime()), deliveryInstance);
        }
        for (LocalTime localTime : itemsByTime.keySet()) {
            OrderDailyFoodByMakersDto.DeliveryGroups deliveryGroups = new OrderDailyFoodByMakersDto.DeliveryGroups();
            // FIXME: OrderItemDailyFood에 LocalTime이 없을 경우?
            List<OrderDailyFoodByMakersDto.FoodBySpot> foodBySpots = toFoodBySpot(itemsByTime.get(localTime));
            deliveryGroups.setPickUpTime(DateUtils.timeToString(localTime));
            deliveryGroups.setFoods(toFood(itemsByTime.get(localTime)));
            deliveryGroups.setFoodCount(deliveryGroups.getFoodCount());
            deliveryGroups.setFoodBySpots(foodBySpots);
            deliveryGroups.setSpotCount(deliveryGroups.getSpotCount());
            deliveryGroupsList.add(deliveryGroups);
        }
        deliveryGroupsList = deliveryGroupsList.stream()
                .sorted(Comparator.comparing(v -> (v.getPickUpTime() != null ? LocalTime.parse(v.getPickUpTime()) : LocalTime.MIN), Comparator.nullsLast(LocalTime::compareTo)))
                .collect(Collectors.toList());
        return deliveryGroupsList;
    }

    default List<OrderDailyFoodByMakersDto.Food> toFood(List<DeliveryInstance> deliveryInstances) {
        MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();
        List<OrderDailyFoodByMakersDto.Food> foodDtoList = new ArrayList<>();
        List<OrderItemDailyFood> orderItemDailyFoods = deliveryInstances.stream()
                .flatMap(deliveryInstance -> deliveryInstance.getOrderItemDailyFoods().stream())
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderStatus()))
                .toList();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if(OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
                foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
            }
        }

        for (Food food : foodMap.keySet()) {
            OrderDailyFoodByMakersDto.Food foodDto = new OrderDailyFoodByMakersDto.Food();
            Integer count = 0;
            if (Optional.ofNullable(foodMap.get(food)).isEmpty()) {
                continue;
            }
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

    default List<OrderDailyFoodByMakersDto.FoodBySpot> toFoodBySpot(List<DeliveryInstance> deliveryInstances) {
        List<OrderDailyFoodByMakersDto.FoodBySpot> foodBySpots = new ArrayList<>();
        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            OrderDailyFoodByMakersDto.FoodBySpot foodBySpot = new OrderDailyFoodByMakersDto.FoodBySpot();
            Spot spot = deliveryInstance.getSpot();

            foodBySpot.setDeliveryId(deliveryInstance.getDeliveryCode());
            foodBySpot.setSpotType(GroupDataType.ofClass(Hibernate.getClass(spot)).getCode());
            foodBySpot.setDeliveryTime(DateUtils.timeToString(deliveryInstance.getDeliveryTime()));
            foodBySpot.setAddress1(spot.getAddress().addressToString());
            foodBySpot.setAddress2(spot.getAddress().getAddress3());
            foodBySpot.setSpotName(spot.getName());
            foodBySpot.setGroupName(getGroupName(spot));
            foodBySpot.setUserName(Hibernate.getClass(spot) == CorporationSpot.class ? null : deliveryInstance.getOrderItemDailyFoods().get(0).getOrder().getUser().getName());
            foodBySpot.setPhone(Hibernate.getClass(spot) == CorporationSpot.class ? null : ((OrderDailyFood) Hibernate.unproxy(deliveryInstance.getOrderItemDailyFoods().get(0).getOrder())).getPhone());
            foodBySpot.setFoods(toFood(Collections.singletonList(deliveryInstance)));
            foodBySpot.setFoodCount(foodBySpot.getFoodCount());
            foodBySpots.add(foodBySpot);
        }
        foodBySpots = foodBySpots.stream()
                .sorted(Comparator.comparing(OrderDailyFoodByMakersDto.FoodBySpot::getDeliveryTime))
                .toList();
        return foodBySpots;
    }

    default List<OrderDailyFoodByMakersDto.FoodByDateDiningType> toFoodByDateDiningType(List<DeliveryInstance> deliveryInstances) {
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = deliveryInstances.stream()
                .sorted(Comparator.comparing(DeliveryInstance::getServiceDate)
                        .thenComparing(DeliveryInstance::getDiningType))
                .collect(Collectors.groupingBy(
                        deliveryInstance -> new AbstractMap.SimpleEntry<>(
                                deliveryInstance.getServiceDate(),
                                deliveryInstance.getDiningType()),
                        Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    OrderDailyFoodByMakersDto.FoodByDateDiningType foodByDateDiningType = new OrderDailyFoodByMakersDto.FoodByDateDiningType();
                    foodByDateDiningType.setServiceDate(DateUtils.format(entry.getKey().getKey()));
                    foodByDateDiningType.setDiningType(entry.getKey().getValue().getDiningType());
                    foodByDateDiningType.setFoods(toFood(entry.getValue()));
                    foodByDateDiningType.setTotalCount(entry.getValue().stream()
                            .map(DeliveryInstance::getItemCount)
                            .reduce(0, Integer::sum));
                    return foodByDateDiningType;
                })
                .toList();

        foodByDateDiningTypes = foodByDateDiningTypes.stream()
                .sorted(Comparator.comparing((OrderDailyFoodByMakersDto.FoodByDateDiningType v) -> DateUtils.stringToDate(v.getServiceDate()))
                        .thenComparing(v -> DiningType.ofString(v.getDiningType()))
                )
                .collect(Collectors.toList());
        return foodByDateDiningTypes;
    }
    default List<OrderDailyFoodByMakersDto.Foods> toFoods(List<DeliveryInstance> deliveryInstances) {
        MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();
        List<OrderItemDailyFood> orderItemDailyFoodList = deliveryInstances.stream()
                .flatMap(deliveryInstance -> deliveryInstance.getDailyFoodDeliveries().stream())
                .map(DailyFoodDelivery::getOrderItemDailyFood)
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderStatus()))
                .toList();

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
            foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
        }
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

    default String getGroupName(Spot spot) {
        if(spot instanceof CorporationSpot corporationSpot) {
            return "(" + corporationSpot.getGroup().getId() + ") " + spot.getGroup().getName();
        }
        if(spot instanceof OpenGroupSpot openGroupSpot) {
            return spot.getGroup().getName();
        }
        return null;
    }
}
