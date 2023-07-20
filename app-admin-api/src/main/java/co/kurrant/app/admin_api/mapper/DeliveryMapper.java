package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.entity.enums.DeliveryStatus;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DeliveryVo;
import co.kurrant.app.admin_api.dto.delivery.ServiceDateVo;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface DeliveryMapper {

    default List<DeliveryVo.DeliveryInfo> getDeliveryInfoList(Collection<DeliveryInstance> deliveryInstances) {
        return deliveryInstances.stream()
                .collect(Collectors.groupingBy(v -> new ServiceDateVo(v.getServiceDate(), v.getDeliveryTime())))
                .entrySet().stream()
                .map(entry -> {
                    ServiceDateVo serviceDateVo = entry.getKey();
                    List<DeliveryInstance> instances = entry.getValue();

                    DeliveryVo.DeliveryInfo deliveryInfo = new DeliveryVo.DeliveryInfo();
                    deliveryInfo.setDeliveryTime(DateUtils.timeToString(serviceDateVo.getDeliveryTime()));
                    deliveryInfo.setServiceDate(DateUtils.localDateToString(serviceDateVo.getServiceDate()));
                    deliveryInfo.setGroup(toDeliveryGroup(instances));
                    return deliveryInfo;
                })
                .sorted(Comparator.comparing(DeliveryVo.DeliveryInfo::getServiceDate)
                        .thenComparing(DeliveryVo.DeliveryInfo::getDeliveryTime))
                .collect(Collectors.toList());
    }

    default List<DeliveryVo.DeliveryGroup> toDeliveryGroup(List<DeliveryInstance> deliveryInstances) {
        return deliveryInstances.stream()
                .collect(Collectors.groupingBy(DeliveryInstance::getSpot))
                .entrySet().stream()
                .map(entry -> {
                    Spot spot = entry.getKey();
                    List<DeliveryInstance> instancesBySpot = entry.getValue();
                    DeliveryInstance firstInstance = instancesBySpot.get(0);

                    DeliveryStatus deliveryStatus = instancesBySpot.stream()
                            .map(DeliveryInstance::getDeliveryStatus)
                            .filter(v -> v.equals(DeliveryStatus.WAIT_DELIVERY) || v.equals(DeliveryStatus.REQUEST_DELIVERED))
                            .findAny()
                            .orElse(DeliveryStatus.DELIVERED);

                    DeliveryVo.DeliveryGroup deliveryGroup = new DeliveryVo.DeliveryGroup();
                    deliveryGroup.setGroupId(spot.getGroup().getId());
                    deliveryGroup.setGroupName(spot.getGroup().getName());
                    deliveryGroup.setDiningType(firstInstance.getDiningType().getCode());
                    deliveryGroup.setSpotId(spot.getId());
                    deliveryGroup.setDeliveryStatus(deliveryStatus.getCode()); // Consider if this line is really necessary since it always sets null
                    deliveryGroup.setSpotName(spot.getName());
                    deliveryGroup.setAddress(spot.getAddress().addressToString());
                    deliveryGroup.setMakersList(toDeliveryMakers(instancesBySpot));

                    return deliveryGroup;
                })
                .sorted(Comparator.comparing(DeliveryVo.DeliveryGroup::getSpotId))
                .collect(Collectors.toList());
    }

    default List<DeliveryVo.DeliveryMakers> toDeliveryMakers(List<DeliveryInstance> deliveryInstances) {
        return deliveryInstances.stream()
                .collect(Collectors.groupingBy(DeliveryInstance::getMakers))
                .entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(deliveryInstanceByMaker -> {
                            Makers makers = entry.getKey();
                            DeliveryVo.DeliveryMakers deliveryMakers = new DeliveryVo.DeliveryMakers();

                            deliveryMakers.setMakersId(makers.getId());
                            deliveryMakers.setMakersName(makers.getName());
                            deliveryMakers.setAddress(makers.getAddress().addressToString());
                            deliveryMakers.setPickupTime(DateUtils.timeToString(deliveryInstanceByMaker.getPickupTime(deliveryInstanceByMaker.getDeliveryTime())));
                            deliveryMakers.setFoods(toDeliveryFood(deliveryInstanceByMaker));
                            deliveryMakers.setTotalCount(deliveryMakers.getCount(deliveryMakers.getFoods()));

                            return deliveryMakers;
                        }))
                .sorted(Comparator.comparing(v -> (v.getPickupTime() != null ? LocalTime.parse(v.getPickupTime()) : LocalTime.MIN), Comparator.nullsLast(LocalTime::compareTo)))
                .collect(Collectors.toList());
    }


    default List<DeliveryVo.DeliveryFood> toDeliveryFood(DeliveryInstance deliveryInstance) {
        return deliveryInstance.getOrderItemDailyFoods().stream()
                .filter(orderItemDailyFood -> OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()))
                .map(OrderItemDailyFood::getDailyFood)
                .distinct() // This is used to mimic the set behavior of your previous implementation
                .map(dailyFood -> {
                    DeliveryVo.DeliveryFood deliveryFood = new DeliveryVo.DeliveryFood();

                    deliveryFood.setFoodId(dailyFood.getFood().getId());
                    deliveryFood.setFoodName(dailyFood.getFood().getName());
                    deliveryFood.setFoodCount(deliveryInstance.getItemCount(dailyFood));

                    return deliveryFood;
                })
                .collect(Collectors.toList());
    }


    @Named("getAddress")
    default String getAddress(Address address) {
        if (address == null) return null;
        StringBuilder addressBuider = new StringBuilder();
        addressBuider.append(address.getAddress1()).append(", ").append(address.getAddress2());
        return String.valueOf(addressBuider);
    }

    default DeliveryVo.DeliveryManifest toDeliveryManifest(DailyFoodDelivery dailyFoodDelivery) {
        return DeliveryVo.DeliveryManifest.builder()
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

    default List<DeliveryVo.DeliveryManifest> toDeliveryManifests(List<DailyFoodDelivery> dailyFoodDeliveries) {
        return dailyFoodDeliveries.stream()
                .map(this::toDeliveryManifest)
                .sorted(Comparator.comparing(DeliveryVo.DeliveryManifest::getServiceDate))
                .toList();
    }

    default List<DeliveryVo.DeliveryInfo> getDeliveryInfoListByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        MultiValueMap<ServiceDateVo, OrderItemDailyFood> serviceDateDtoMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            serviceDateDtoMap.add(new ServiceDateVo(orderItemDailyFood.getDailyFood().getServiceDate(), orderItemDailyFood.getDeliveryTime()), orderItemDailyFood);
        }

        List<DeliveryVo.DeliveryInfo> deliveryInfoList = new ArrayList<>();
        for (ServiceDateVo serviceDateVo : serviceDateDtoMap.keySet()) {
            DeliveryVo.DeliveryInfo deliveryInfo = new DeliveryVo.DeliveryInfo();

            deliveryInfo.setDeliveryTime(DateUtils.timeToString(serviceDateVo.getDeliveryTime()));
            deliveryInfo.setServiceDate(DateUtils.localDateToString(serviceDateVo.getServiceDate()));
            deliveryInfo.setGroup(toDeliveryGroupByOrderItemDailyFood(serviceDateDtoMap.get(serviceDateVo)));

            deliveryInfoList.add(deliveryInfo);
        }

        return deliveryInfoList.stream().sorted(Comparator.comparing(DeliveryVo.DeliveryInfo::getServiceDate).thenComparing(DeliveryVo.DeliveryInfo::getDeliveryTime)).toList();
    }

    default List<DeliveryVo.DeliveryGroup> toDeliveryGroupByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<DeliveryVo.DeliveryGroup> deliveryGroupList = new ArrayList<>();
        Map<Spot, List<OrderItemDailyFood>> spotMap = orderItemDailyFoods.stream()
                .filter(orderItemDailyFood -> Hibernate.unproxy(orderItemDailyFood.getOrder()) instanceof OrderDailyFood)
                .collect(Collectors.groupingBy(orderItemDailyFood -> ((OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder())).getSpot()));

        for (Spot spot : spotMap.keySet()) {
            DeliveryVo.DeliveryGroup deliveryGroup = new DeliveryVo.DeliveryGroup();

            deliveryGroup.setGroupId(spot.getGroup().getId());
            deliveryGroup.setGroupName(spot.getGroup().getName());
            deliveryGroup.setDiningType(spotMap.get(spot).get(0).getDailyFood().getDiningType().getCode());
            deliveryGroup.setSpotId(spot.getId());
            deliveryGroup.setSpotName(spot.getName());
            deliveryGroup.setAddress(spot.getAddress().addressToString());
            deliveryGroup.setMakersList(toDeliveryMakersByOrderItemDailyFood(spotMap.get(spot)));

            deliveryGroupList.add(deliveryGroup);
        }
        return deliveryGroupList.stream().sorted(Comparator.comparing(DeliveryVo.DeliveryGroup::getSpotId)).toList();
    }

    default List<DeliveryVo.DeliveryMakers> toDeliveryMakersByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<DeliveryVo.DeliveryMakers> deliveryMakersList = new ArrayList<>();
        Map<Makers, List<OrderItemDailyFood>> makersMap = orderItemDailyFoods.stream()
                .collect(Collectors.groupingBy(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getFood().getMakers()));

        for (Makers makers : makersMap.keySet()) {
            DeliveryVo.DeliveryMakers deliveryMakers = new DeliveryVo.DeliveryMakers();

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

    default List<DeliveryVo.DeliveryFood> toDeliveryFoodByOrderItemDailyFood(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<DeliveryVo.DeliveryFood> deliveryFoodList = new ArrayList<>();
        Map<DailyFood, Integer> dailyFoodMap = orderItemDailyFoods.stream()
                .sorted(Comparator.comparing(orderItemDailyFood -> orderItemDailyFood.getDailyFood().getId()))
                .collect(Collectors.groupingBy(OrderItemDailyFood::getDailyFood, Collectors.summingInt(OrderItemDailyFood::getCount)));

        for (DailyFood dailyFood : dailyFoodMap.keySet()) {
            DeliveryVo.DeliveryFood deliveryFood = new DeliveryVo.DeliveryFood();

            deliveryFood.setFoodId(dailyFood.getFood().getId());
            deliveryFood.setFoodName(dailyFood.getFood().getName());
            deliveryFood.setFoodCount(dailyFoodMap.get(dailyFood));

            deliveryFoodList.add(deliveryFood);
        }

        return deliveryFoodList;
    }

}
