package co.dalicious.domain.delivery.mappper;

import co.dalicious.domain.client.entity.CorporationSpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroupSpot;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.dto.DeliveryInstanceDto;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.dto.DeliveryInfoDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.util.FoodUtils;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.dalicious.domain.order.dto.ServiceDiningVo;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, FoodUtils.class})
public interface DeliveryInstanceMapper {
    default List<DeliveryInstance> toEntities(DeliveryInstanceDto deliveryInstanceDto, List<Makers> makers, List<Group> groups) {
        List<DeliveryInstance> deliveryInstances = new ArrayList<>();
        Group group = groups.stream().filter(v -> v.getName().equals(deliveryInstanceDto.getGroupName())).findAny().orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        for (String makersName : deliveryInstanceDto.getMakersNames()) {
            Makers maker = makers.stream().filter(v -> v.getName().equals(makersName)).findAny().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_MATCHED_MAKERS));
            if (group.getSpots().size() > 1) {
                for (Spot spot : group.getSpots()) {
                    deliveryInstances.add(DeliveryInstance.builder()
                            .serviceDate(DateUtils.stringToDate(deliveryInstanceDto.getDeliveryDate()))
                            .deliveryTime(DateUtils.stringToLocalTime(deliveryInstanceDto.getDeliveryTime()))
                            .diningType(DiningType.ofString(deliveryInstanceDto.getDiningType()))
                            .orderNumber(null) // TODO: MySpot 구현 필요
                            .makers(maker)
                            .spot(spot)
                            .build());
                }
            } else {
                deliveryInstances.add(DeliveryInstance.builder()
                        .serviceDate(DateUtils.stringToDate(deliveryInstanceDto.getDeliveryDate()))
                        .deliveryTime(DateUtils.stringToLocalTime(deliveryInstanceDto.getDeliveryTime()))
                        .diningType(DiningType.ofString(deliveryInstanceDto.getDiningType()))
                        .orderNumber(null)
                        .makers(maker)
                        .spot(group.getSpots().get(0))
                        .build());
            }

        }
        return deliveryInstances;
    }

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


    default OrderDailyFoodByMakersDto.ByPeriod toDto(List<DeliveryInstance> deliveryInstances, List<FoodCapacity> foodCapacities) {
        OrderDailyFoodByMakersDto.ByPeriod byPeriod = new OrderDailyFoodByMakersDto.ByPeriod();

        // 1. 메이커스 음식별 개수 및 상세정보
        List<OrderDailyFoodByMakersDto.Foods> foods = toFoods(deliveryInstances);
        byPeriod.setTotalFoods(foods);

        // 2. 메이커스 기간별 음식 개수
        List<OrderDailyFoodByMakersDto.FoodByDateDiningType> foodByDateDiningTypes = toFoodByDateDiningType(deliveryInstances, foodCapacities);
        byPeriod.setFoodByDateDiningTypes(foodByDateDiningTypes);

        // 3. 고객사별 식사일정
        List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> deliveryGroupsByDates = toDeliveryGroupsByDate(deliveryInstances, foodCapacities);
        byPeriod.setDeliveryGroupsByDates(deliveryGroupsByDates);

        return byPeriod;
    }

    default List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> toDeliveryGroupsByDate(List<DeliveryInstance> deliveryInstances, List<FoodCapacity> foodCapacities) {
        List<OrderDailyFoodByMakersDto.DeliveryGroupsByDate> deliveryGroupsByDates = new ArrayList<>();
        MultiValueMap<ServiceDiningVo, DeliveryInstance> deliveryGroupsByDatesMap = new LinkedMultiValueMap<>();
        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            ServiceDiningVo serviceDiningVo = new ServiceDiningVo(deliveryInstance.getServiceDate(), deliveryInstance.getDiningType());
            deliveryGroupsByDatesMap.add(serviceDiningVo, deliveryInstance);
        }

        for (ServiceDiningVo serviceDiningVo : deliveryGroupsByDatesMap.keySet()) {
            OrderDailyFoodByMakersDto.DeliveryGroupsByDate deliveryGroupsByDate = new OrderDailyFoodByMakersDto.DeliveryGroupsByDate();
            deliveryGroupsByDate.setServiceDate(DateUtils.format(serviceDiningVo.getServiceDate()));
            deliveryGroupsByDate.setDiningType(serviceDiningVo.getDiningType().getDiningType());
            deliveryGroupsByDate.setSpotCount(deliveryGroupsByDatesMap.get(serviceDiningVo).stream().map(DeliveryInstance::getSpot).collect(Collectors.toSet()).size());
            deliveryGroupsByDate.setDeliveryGroups(toDeliveryGroups(deliveryInstances));

            LocalDateTime lastOrderTime = FoodUtils.getLastOrderTime(deliveryGroupsByDatesMap.get(serviceDiningVo).get(0).getMakers(), serviceDiningVo.getDiningType(), serviceDiningVo.getServiceDate(), foodCapacities);
            deliveryGroupsByDate.setLastOrderTime(DateUtils.localDateTimeToString(lastOrderTime));
            if (lastOrderTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Seoul"))))
                deliveryGroupsByDate.setBeforeLastOrderTime(false);
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
            if (OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) {
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
        MultiValueMap<LocalTime, DeliveryInstance> deliveryMap = new LinkedMultiValueMap<>();
        List<OrderDailyFoodByMakersDto.FoodBySpot> foodBySpots = new ArrayList<>();
        for (DeliveryInstance deliveryInstance : deliveryInstances) {
            deliveryMap.add(deliveryInstance.getDeliveryTime(), deliveryInstance);
        }
        for (LocalTime deliveryTime : deliveryMap.keySet()) {
            List<DeliveryInstance> deliveryInstanceList = deliveryMap.get(deliveryTime);
            for (DeliveryInstance deliveryInstance : deliveryInstanceList) {
                List<OrderDailyFoodByMakersDto.Food> foods = toFood(Collections.singletonList(deliveryInstance));
                if(!foods.isEmpty()) {
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
                    foodBySpot.setFoods(foods);
                    foodBySpot.setFoodCount(foodBySpot.getFoodCount());
                    foodBySpots.add(foodBySpot);
                }
            }
        }
        foodBySpots = foodBySpots.stream()
                .sorted(Comparator.comparing(OrderDailyFoodByMakersDto.FoodBySpot::getDeliveryTime)
                        .thenComparing(v -> (v != null) ? Integer.parseInt(v.getDeliveryId()) : 0))
                .toList();
        return foodBySpots;
    }

    default List<OrderDailyFoodByMakersDto.FoodByDateDiningType> toFoodByDateDiningType(List<DeliveryInstance> deliveryInstances, List<FoodCapacity> foodCapacities) {
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
                    LocalDateTime lastOrderTime = FoodUtils.getLastOrderTime(entry.getValue().get(0).getMakers(), entry.getKey().getValue(), entry.getKey().getKey(), foodCapacities);
                    foodByDateDiningType.setLastOrderTime(DateUtils.localDateTimeToString(lastOrderTime));
                    if (lastOrderTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Seoul"))))
                        foodByDateDiningType.setBeforeLastOrderTime(false);
                    foodByDateDiningType.setBeforeLastOrderTime(true);

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

    @Transactional
    default List<DeliveryInstanceDto> toScheduleDtos(List<DeliveryInfoDto> deliveryInfoDtos, List<DeliveryInstance> deliveryInstances) {
        List<DeliveryInstanceDto> deliveryInstanceDtos = new ArrayList<>();
        MultiValueMap<DeliveryInfoDto.Key, DeliveryInfoDto> deliveryInfoDtoMap = new LinkedMultiValueMap<>();
        for (DeliveryInfoDto deliveryInfoDto : deliveryInfoDtos) {
            deliveryInfoDtoMap.add(new DeliveryInfoDto.Key(deliveryInfoDto), deliveryInfoDto);
        }
        // 식사일정과 그룹이 같고, 메이커스만 다른 DeliveryInstance를 가져온다.
        for (DeliveryInfoDto.Key key : deliveryInfoDtoMap.keySet()) {
            List<DeliveryInfoDto> deliveryInfoDtoList = deliveryInfoDtoMap.get(key);
            List<DeliveryInstance> selectedDeliveryInstances = deliveryInstances.stream()
                    .filter(v -> v.getServiceDate().equals(key.getServiceDate()) &&
                            v.getDiningType().equals(key.getDiningType()) &&
                            v.getDeliveryTime().equals(key.getDeliveryTime()) &&
                            v.getSpot().getGroup().equals(key.getGroup()))
                    .toList();
            if (selectedDeliveryInstances.isEmpty()) {
                deliveryInstanceDtos.add(toScheduleDtoByDailyFood(Objects.requireNonNull(deliveryInfoDtoList)));
                continue;
            }
            for (DeliveryInstance selectedDeliveryInstance : selectedDeliveryInstances) {
                deliveryInfoDtoList.removeIf(v -> v.hasSameValue(selectedDeliveryInstance.getServiceDate(), selectedDeliveryInstance.getDiningType(), selectedDeliveryInstance.getSpot().getGroup(), selectedDeliveryInstance.getMakers(), selectedDeliveryInstance.getDeliveryTime()));
            }
            if (!deliveryInfoDtoList.isEmpty()) {
                deliveryInstanceDtos.add(toScheduleDtoByDailyFood(Objects.requireNonNull(deliveryInfoDtoList)));
            }
            deliveryInstanceDtos.addAll(toScheduleDtoByDeliveryInstance(selectedDeliveryInstances));
        }
        return deliveryInstanceDtos;
    }

    default DeliveryInstanceDto toScheduleDtoByDailyFood(List<DeliveryInfoDto> deliveryInfoDto) {
        Set<String> makersNames = deliveryInfoDto.stream()
                .map(v -> v.getMakers().getName())
                .collect(Collectors.toSet());
        ;

        return DeliveryInstanceDto.builder()
                .id(generateTempId(deliveryInfoDto.get(0)))
                .deliveryDate(DateUtils.localDateToString(deliveryInfoDto.get(0).getServiceDate()))
                .diningType(deliveryInfoDto.get(0).getDiningType().getDiningType())
                .deliveryTime(DateUtils.timeToString(deliveryInfoDto.get(0).getDeliveryTime()))
                .groupName(deliveryInfoDto.get(0).getGroup().getName())
                .makersNames(makersNames)
                .driver(null)
                .build();
    }

    default List<DeliveryInstanceDto> toScheduleDtoByDeliveryInstance(List<DeliveryInstance> deliveryInstances) {
        return deliveryInstances.stream()
                .collect(Collectors.groupingBy(
                        deliveryInstance -> Optional.ofNullable(deliveryInstance.getDriver()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<DeliveryInstance> instances = entry.getValue();
                    Set<String> makersName = instances.stream()
                            .map(deliveryInstance -> deliveryInstance.getMakers().getName())
                            .collect(Collectors.toSet());
                    return DeliveryInstanceDto.builder()
                            .id(instances.get(0).getId().toString())
                            .deliveryDate(DateUtils.localDateToString(instances.get(0).getServiceDate()))
                            .diningType(instances.get(0).getDiningType().getDiningType())
                            .deliveryTime(DateUtils.timeToString(instances.get(0).getDeliveryTime()))
                            .groupName(instances.get(0).getSpot().getGroup().getName())
                            .makersNames(makersName)
                            .driver(entry.getKey().isPresent() ? entry.getKey().get().getName() : null)
                            .build();
                }).toList();
    }

    default String generateTempId(DeliveryInfoDto deliveryInfoDto) {
        return "temp"
                + DateUtils.formatWithoutSeparator(deliveryInfoDto.getServiceDate())
                + deliveryInfoDto.getDiningType().getCode() + deliveryInfoDto.getDeliveryTime().getHour()
                + deliveryInfoDto.getDeliveryTime().getMinute()
                + deliveryInfoDto.getGroup().getId() + "_1";
    }

    default String getGroupName(Spot spot) {
        if (spot instanceof CorporationSpot corporationSpot) {
            return "(" + corporationSpot.getGroup().getId() + ") " + spot.getGroup().getName();
        }
        if (spot instanceof OpenGroupSpot openGroupSpot) {
            return spot.getGroup().getName();
        }
        return null;
    }
}
