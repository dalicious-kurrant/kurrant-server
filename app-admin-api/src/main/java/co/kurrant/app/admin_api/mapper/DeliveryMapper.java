package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.dto.delivery.ServiceDateDto;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface DeliveryMapper {

    default List<DeliveryDto.DeliveryInfo> getDeliveryInfoList(List<DeliveryInstance> deliveryInstances) {
        Map<ServiceDateDto, List<DeliveryInstance>> serviceDateMap = deliveryInstances.stream().collect(Collectors.groupingBy(v -> new ServiceDateDto(v.getServiceDate(), v.getDeliveryTime())));

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for(ServiceDateDto serviceDateDto : serviceDateMap.keySet()) {
            DeliveryDto.DeliveryInfo deliveryInfo = new DeliveryDto.DeliveryInfo();

            deliveryInfo.setDeliveryTime(DateUtils.timeToString(serviceDateDto.getDeliveryTime()));
            deliveryInfo.setServiceDate(DateUtils.localDateToString(serviceDateDto.getServiceDate()));
            deliveryInfo.setGroup(toDeliveryGroup(serviceDateMap.get(serviceDateDto)));

            deliveryInfoList.add(deliveryInfo);
        }

        return deliveryInfoList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryInfo::getServiceDate).thenComparing(DeliveryDto.DeliveryInfo::getDeliveryTime)).toList();
    }

    default List<DeliveryDto.DeliveryGroup> toDeliveryGroup(List<DeliveryInstance> deliveryInstances) {
        List<DeliveryDto.DeliveryGroup> deliveryGroupList = new ArrayList<>();
        Map<Spot, List<DeliveryInstance>> spotMap = deliveryInstances.stream().collect(Collectors.groupingBy(DeliveryInstance::getSpot));

        for(Spot spot : spotMap.keySet()) {
            DeliveryDto.DeliveryGroup deliveryGroup = new DeliveryDto.DeliveryGroup();

            deliveryGroup.setGroupId(spot.getGroup().getId());
            deliveryGroup.setGroupName(spot.getGroup().getName());
            deliveryGroup.setDiningType(spotMap.get(spot).get(0).getDiningType().getCode());
            deliveryGroup.setSpotId(spot.getId());
            deliveryGroup.setSpotName(spot.getName());
            deliveryGroup.setAddress(spot.getAddress().addressToString());
            deliveryGroup.setMakersList(toDeliveryMakers(spotMap.get(spot)));

            deliveryGroupList.add(deliveryGroup);
        }
        return deliveryGroupList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryGroup::getSpotId)).toList();
    }

    default List<DeliveryDto.DeliveryMakers> toDeliveryMakers(List<DeliveryInstance> deliveryInstances){
        List<DeliveryDto.DeliveryMakers> deliveryMakersList = new ArrayList<>();
        Map<Makers, DeliveryInstance> makersMap = deliveryInstances.stream().collect(Collectors.toMap(DeliveryInstance::getMakers, Function.identity()));

        for(Makers makers : makersMap.keySet()) {
            DeliveryDto.DeliveryMakers deliveryMakers = new DeliveryDto.DeliveryMakers();

            deliveryMakers.setMakersId(makers.getId());
            deliveryMakers.setMakersName(makers.getName());
            deliveryMakers.setAddress(makers.getAddress().addressToString());
            deliveryMakers.setPickupTime(DateUtils.timeToString(makersMap.get(makers).getPickupTime(makersMap.get(makers).getDeliveryTime())));
            deliveryMakers.setFoods(toDeliveryFood(makersMap.get(makers)));
            deliveryMakers.setTotalCount(deliveryMakers.getCount(deliveryMakers.getFoods()));

            deliveryMakersList.add(deliveryMakers);
        }
        return deliveryMakersList.stream().sorted(Comparator.comparing(v -> (v.getPickupTime() != null ? LocalTime.parse(v.getPickupTime()) : LocalTime.MIN), Comparator.nullsLast(LocalTime::compareTo))).toList();
    }

    default List<DeliveryDto.DeliveryFood> toDeliveryFood(DeliveryInstance deliveryInstance) {
        List<DeliveryDto.DeliveryFood> deliveryFoodList = new ArrayList<>();
        List<DailyFood> dailyFoodList = deliveryInstance.getOrderItemDailyFoods().stream()
                .filter(orderItemDailyFood -> OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()))
                .map(OrderItemDailyFood::getDailyFood)
                .toList();

        for(DailyFood dailyFood : dailyFoodList) {
            DeliveryDto.DeliveryFood deliveryFood = new DeliveryDto.DeliveryFood();

            deliveryFood.setFoodId(dailyFood.getFood().getId());
            deliveryFood.setFoodName(dailyFood.getFood().getName());
            deliveryFood.setFoodCount(deliveryInstance.getItemCount(dailyFood));

            deliveryFoodList.add(deliveryFood);
        }

        return deliveryFoodList;
    }

    @Named("getAddress")
    default String getAddress(Address address) {
        if(address == null) return null;
        StringBuilder addressBuider = new StringBuilder();
        addressBuider.append(address.getAddress1()).append(", ").append(address.getAddress2());
        return String.valueOf(addressBuider);
    }

    default DeliveryDto.DeliveryManifest toDeliveryManifest(DailyFoodDelivery dailyFoodDelivery) {
        return DeliveryDto.DeliveryManifest.builder()
                .spotType(GroupDataType.ofClass(Hibernate.getClass(dailyFoodDelivery.getDeliveryInstance().getSpot())).getType())
                .serviceDate(DateUtils.format(dailyFoodDelivery.getDeliveryInstance().getServiceDate()))
                .diningType(dailyFoodDelivery.getDeliveryInstance().getDiningType().getCode())
                .deliveryTime(DateUtils.timeToString(dailyFoodDelivery.getDeliveryInstance().getDeliveryTime()))
                .orderNumber(dailyFoodDelivery.getDeliveryInstance().getDeliveryCode())
                .makersName(dailyFoodDelivery.getDeliveryInstance().getMakers().getName())
                .makersAddress(dailyFoodDelivery.getDeliveryInstance().getMakers().getAddress().addressToString())
                .makersPhone(dailyFoodDelivery.getDeliveryInstance().getMakers().getCEOPhone())
                .foodName(dailyFoodDelivery.getOrderItemDailyFood().getName())
                .count(dailyFoodDelivery.getOrderItemDailyFood().getCount())
                .userName(dailyFoodDelivery.getOrderItemDailyFood().getOrder().getUser().getName())
                .userAddress(dailyFoodDelivery.getOrderItemDailyFood().getOrder().getAddress().addressToString())
                .userPhone(((OrderDailyFood) Hibernate.unproxy(dailyFoodDelivery.getOrderItemDailyFood().getOrder())).getPhone())
                .memo(((OrderDailyFood) Hibernate.unproxy(dailyFoodDelivery.getOrderItemDailyFood().getOrder())).getMemo()) // 추후수정
                .build();
    }

    default List<DeliveryDto.DeliveryManifest> toDeliveryManifests(List<DailyFoodDelivery> dailyFoodDeliveries) {
        return dailyFoodDeliveries.stream()
                .map(this::toDeliveryManifest)
                .sorted(Comparator.comparing(DeliveryDto.DeliveryManifest::getServiceDate))
                .toList();
    }

    default List<DeliveryDto.DeliveryInfo> getDeliveryInfoListByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<ServiceDateDto, OrderItemDailyFood> serviceDateDtoMap = new LinkedMultiValueMap<>();
        for(OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            serviceDateDtoMap.add(new ServiceDateDto(orderItemDailyFood.getDailyFood().getServiceDate(), orderItemDailyFood.getDeliveryTime()), orderItemDailyFood);
        }

        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for(ServiceDateDto serviceDateDto : serviceDateDtoMap.keySet()) {
            DeliveryDto.DeliveryInfo deliveryInfo = new DeliveryDto.DeliveryInfo();

            deliveryInfo.setDeliveryTime(DateUtils.timeToString(serviceDateDto.getDeliveryTime()));
            deliveryInfo.setServiceDate(DateUtils.localDateToString(serviceDateDto.getServiceDate()));
            deliveryInfo.setGroup(toDeliveryGroupByOrderItemDailyFood(serviceDateDtoMap.get(serviceDateDto)));

            deliveryInfoList.add(deliveryInfo);
        }

        return deliveryInfoList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryInfo::getServiceDate).thenComparing(DeliveryDto.DeliveryInfo::getDeliveryTime)).toList();
    }

    default List<DeliveryDto.DeliveryGroup> toDeliveryGroupByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<DeliveryDto.DeliveryGroup> deliveryGroupList = new ArrayList<>();
        Map<Spot, List<OrderItemDailyFood>> spotMap = orderItemDailyFoods.stream()
                .filter(orderItemDailyFood -> Hibernate.unproxy(orderItemDailyFood.getOrder()) instanceof OrderDailyFood)
                .collect(Collectors.groupingBy(orderItemDailyFood -> ((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot()));

        for(Spot spot : spotMap.keySet()) {
            DeliveryDto.DeliveryGroup deliveryGroup = new DeliveryDto.DeliveryGroup();

            deliveryGroup.setGroupId(spot.getGroup().getId());
            deliveryGroup.setGroupName(spot.getGroup().getName());
            deliveryGroup.setDiningType(spotMap.get(spot).get(0).getDailyFood().getDiningType().getCode());
            deliveryGroup.setSpotId(spot.getId());
            deliveryGroup.setSpotName(spot.getName());
            deliveryGroup.setAddress(spot.getAddress().addressToString());
            deliveryGroup.setMakersList(toDeliveryMakersByOrderItemDailyFood(spotMap.get(spot)));

            deliveryGroupList.add(deliveryGroup);
        }
        return deliveryGroupList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryGroup::getSpotId)).toList();
    }

    default List<DeliveryDto.DeliveryMakers> toDeliveryMakersByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods){
        List<DeliveryDto.DeliveryMakers> deliveryMakersList = new ArrayList<>();
        Map<Makers, List<OrderItemDailyFood>> makersMap = orderItemDailyFoods.stream()
                .collect(Collectors.groupingBy(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getFood().getMakers()));

        for(Makers makers : makersMap.keySet()) {
            DeliveryDto.DeliveryMakers deliveryMakers = new DeliveryDto.DeliveryMakers();

            deliveryMakers.setMakersId(makers.getId());
            deliveryMakers.setMakersName(makers.getName());
            deliveryMakers.setAddress(makers.getAddress().addressToString());

            LocalTime pickupTime = makersMap.get(makers).stream()
                    .flatMap(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getDailyFoodGroup().getDeliverySchedules().stream())
                    .filter(deliverySchedule -> deliverySchedule.getDeliveryTime().equals(orderItemDailyFoods.get(0).getDeliveryTime()))
                    .map(DeliverySchedule::getPickupTime)
                    .findFirst()
                    .orElse(null);

            deliveryMakers.setPickupTime(DateUtils.timeToString(pickupTime));
            deliveryMakers.setFoods(toDeliveryFoodByOrderItemDailyFood(makersMap.get(makers)));
            deliveryMakers.setTotalCount(deliveryMakers.getCount(deliveryMakers.getFoods()));

            deliveryMakersList.add(deliveryMakers);
        }
        return deliveryMakersList.stream().sorted(Comparator.comparing(v -> (v.getPickupTime() != null ? LocalTime.parse(v.getPickupTime()) : LocalTime.MIN), Comparator.nullsLast(LocalTime::compareTo))).toList();
    }

    default List<DeliveryDto.DeliveryFood> toDeliveryFoodByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<DeliveryDto.DeliveryFood> deliveryFoodList = new ArrayList<>();
        Map<DailyFood, Integer> dailyFoodMap = orderItemDailyFoods.stream()
                .sorted(Comparator.comparing(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getId()))
                .collect(Collectors.groupingBy(OrderItemDailyFood::getDailyFood, Collectors.summingInt(OrderItemDailyFood::getCount)));

        for(DailyFood dailyFood : dailyFoodMap.keySet()) {
            DeliveryDto.DeliveryFood deliveryFood = new DeliveryDto.DeliveryFood();

            deliveryFood.setFoodId(dailyFood.getFood().getId());
            deliveryFood.setFoodName(dailyFood.getFood().getName());
            deliveryFood.setFoodCount(dailyFoodMap.get(dailyFood));

            deliveryFoodList.add(deliveryFood);
        }

        return deliveryFoodList;
    }

}
