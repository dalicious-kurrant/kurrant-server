package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
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

        return serviceDateMap.keySet().stream()
                .map(serviceDate -> {
                    AtomicReference<List<DeliveryDto.DeliveryGroup>> deliveryGroupList = new AtomicReference<>(new ArrayList<>());
                        Map<Spot, List<DeliveryInstance>> spotListMap = serviceDateMap.get(serviceDate).stream().collect(Collectors.groupingBy(DeliveryInstance::getSpot));
                        deliveryGroupList.set(spotListMap.values().stream()
                                .map(instances -> {
                                    Map<Makers, DeliveryInstance> makersListMap = instances.stream().collect(Collectors.toMap(DeliveryInstance::getMakers, Function.identity()));
                                    List<DeliveryDto.DeliveryMakers> deliveryMakersList = makersListMap.values().stream()
                                            .map(values -> {
                                                List<DailyFood> dailyFoodList = values.getOrderItemDailyFoods().stream()
                                                        .filter(orderItemDailyFood -> OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus()))
                                                        .map(OrderItemDailyFood::getDailyFood)
                                                        .toList();
                                                List<DeliveryDto.DeliveryFood> deliveryFoodList = dailyFoodList.stream()
                                                        .map(v -> toDeliveryFood(v, values.getItemCount(v)))
                                                        .sorted(Comparator.comparing(DeliveryDto.DeliveryFood::getFoodId))
                                                        .toList();

                                                DeliveryDto.DeliveryMakers deliveryMakers = toDeliveryMakers(values);
                                                deliveryMakers.setFoods(deliveryFoodList);
                                                deliveryMakers.setPickupTime(DateUtils.timeToString(dailyFoodList.get(0).getDailyFoodGroup().getPickUpTime(serviceDate.getDeliveryTime())));
                                                return deliveryMakers;
                                            }).toList();

                                    DeliveryDto.DeliveryGroup deliveryGroup = toDeliveryGroup(instances.get(0));
                                    List<DeliveryDto.DeliveryMakers> deliveryMakers = deliveryMakersList.stream()
                                            .sorted(Comparator.comparing(v -> v.getPickupTime() != null ? LocalTime.parse(v.getPickupTime()) : null))
                                            .toList();
                                    deliveryGroup.setMakersList(deliveryMakers);
                                    return deliveryGroup;
                                }).toList());
                    return toDeliveryInfo(serviceDate, deliveryGroupList.get().stream().sorted(Comparator.comparing(DeliveryDto.DeliveryGroup::getSpotId)).toList());
                })
                .sorted(Comparator.comparing(DeliveryDto.DeliveryInfo::getServiceDate).thenComparing(DeliveryDto.DeliveryInfo::getDeliveryTime))
                .toList();
    }

    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(target = "diningType", expression = "java(dto.getDiningType().getCode())")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "spot.address", target = "address", qualifiedByName = "getAddress")
    DeliveryDto.DeliveryGroup toDeliveryGroup(DeliveryInstance dto);

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
        return deliveryGroupList;
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
        }
        return deliveryMakersList;
    };

    default DeliveryDto.DeliveryFood toDeliveryFood(DeliveryInstance deliveryInstance) {

    };

    @Named("getAddress")
    default String getAddress(Address address) {
        if(address == null) return null;
        StringBuilder addressBuider = new StringBuilder();
        addressBuider.append(address.getAddress1()).append(", ").append(address.getAddress2());
        return String.valueOf(addressBuider);
    }

    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(source = "dailyFood.food.id", target = "foodId")
    @Mapping(source = "count", target = "foodCount")
    DeliveryDto.DeliveryFood toDeliveryFood(DailyFood dailyFood, Integer count);

    @Mapping(source = "makers.id", target = "makersId")
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(target = "address", expression = "java(dto.getMakers().getAddress().addressToString())")
    DeliveryDto.DeliveryMakers toDeliveryMakers(DeliveryInstance dto);

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

    // TODO: 추후 삭제 (DeliveryInstance 활성화시)
    @Mapping(source = "serviceDateDto.serviceDate", target = "serviceDate")
    @Mapping(source = "serviceDateDto.deliveryTime", target = "deliveryTime")
    @Mapping(source = "deliveryGroupList", target = "group")
    DeliveryDto.DeliveryInfo toDeliveryInfo(ServiceDateDto serviceDateDto, List<DeliveryDto.DeliveryGroup> deliveryGroupList);

    @Mapping(source = "makers.id", target = "makersId")
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(source = "pickupTime", target = "pickupTime")
    @Mapping(source = "deliveryFoodList", target = "foods")
    @Mapping(target = "address", expression = "java(makers.getAddress().addressToString())")
    DeliveryDto.DeliveryMakers toDeliveryMakers(Makers makers, List<DeliveryDto.DeliveryFood> deliveryFoodList, LocalTime pickupTime);
    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "spot.address", target = "address", qualifiedByName = "getAddress")
    @Mapping(source = "deliveryMakersList", target = "makersList")
    DeliveryDto.DeliveryGroup toDeliveryGroup(Spot spot, Integer diningType, LocalTime deliveryTime, List<DeliveryDto.DeliveryMakers> deliveryMakersList);
}
