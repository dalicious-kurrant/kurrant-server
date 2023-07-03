package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface DeliveryMapper {

    default List<DeliveryDto.DeliveryInfo> getDeliveryInfoList(List<DeliveryInstance> deliveryInstances) {
        Map<LocalDate, List<DeliveryInstance>> serviceDateMap = deliveryInstances.stream().collect(Collectors.groupingBy(DeliveryInstance::getServiceDate));

        return serviceDateMap.entrySet().stream()
                .map(serviceDate -> {
                    Map<LocalTime, List<DeliveryInstance>> deliveryTimeMap = serviceDate.getValue().stream().collect(Collectors.groupingBy(DeliveryInstance::getDeliveryTime));

                    AtomicReference<List<DeliveryDto.DeliveryGroup>> deliveryGroupList = new AtomicReference<>(new ArrayList<>());
                    deliveryTimeMap.forEach((key, value) -> {
                        Map<Spot, List<DeliveryInstance>> spotListMap = value.stream().collect(Collectors.groupingBy(DeliveryInstance::getSpot));
                        deliveryGroupList.set(spotListMap.values().stream()
                                .map(instances -> {
                                    Map<Makers, List<DeliveryInstance>> makersListMap = instances.stream().collect(Collectors.groupingBy(DeliveryInstance::getMakers));
                                    List<DeliveryDto.DeliveryMakers> deliveryMakersList = makersListMap.values().stream()
                                            .map(deliveryInstanceList -> {
                                                List<DeliveryDto.DeliveryFood> deliveryFoodList = deliveryInstanceList.stream()
                                                        .map(v -> toDeliveryFood(v.getOrderItemDailyFoods().get(0).getDailyFood(), v.getItemCount()))
                                                        .sorted(Comparator.comparing(DeliveryDto.DeliveryFood::getFoodId))
                                                        .toList();

                                                DeliveryDto.DeliveryMakers deliveryMakers = toDeliveryMakers(deliveryInstanceList.get(0));
                                                deliveryMakers.setFoods(deliveryFoodList);
                                                return deliveryMakers;
                                            }).toList();

                                    DeliveryDto.DeliveryGroup deliveryGroup = toDeliveryGroup(instances.get(0));
                                    deliveryGroup.setMakersList(deliveryMakersList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryMakers::getPickupTime)).toList());
                                    return deliveryGroup;
                                }).toList());
                    });
                    return toDeliveryInfo(serviceDate.getKey(), deliveryGroupList.get().stream().sorted(Comparator.comparing(DeliveryDto.DeliveryGroup::getDeliveryTime)).toList());
                })
                .sorted(Comparator.comparing(DeliveryDto.DeliveryInfo::getServiceDate))
                .toList();
    }

    @Mapping(source = "deliveryGroupList", target = "group")
    DeliveryDto.DeliveryInfo toDeliveryInfo(LocalDate serviceDate, List<DeliveryDto.DeliveryGroup> deliveryGroupList);

    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "deliveryTime", target = "deliveryTime")
    @Mapping(target = "diningType", expression = "java(dto.getDiningType().getCode())")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "spot.address", target = "address", qualifiedByName = "getAddress")
    DeliveryDto.DeliveryGroup toDeliveryGroup(DeliveryInstance dto);

    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "deliveryTime", target = "deliveryTime")
    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(target = "address", expression = "java(spot.getAddress().addressToString())")
    @Mapping(source = "deliveryMakersList", target = "makersList")
    DeliveryDto.DeliveryGroup toDeliveryGroup(Spot spot, Integer diningType, LocalTime deliveryTime, List<DeliveryDto.DeliveryMakers> deliveryMakersList);

    @Named("getAddress")
    default String getAddress(Address address) {
        if(address == null) return null;
        return String.valueOf(address.getAddress1() + ", " + address.getAddress2());
    }

    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(source = "dailyFood.food.id", target = "foodId")
    @Mapping(source = "count", target = "foodCount")
    DeliveryDto.DeliveryFood toDeliveryFood(DailyFood dailyFood, Integer count);

    @Mapping(source = "makers.id", target = "makersId")
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(target = "address", expression = "java(dto.getMakers().getAddress().addressToString())")
    DeliveryDto.DeliveryMakers toDeliveryMakers(DeliveryInstance dto);

    @Mapping(source = "makers.id", target = "makersId")
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(source = "pickupTime", target = "pickupTime")
    @Mapping(source = "deliveryFoodList", target = "foods")
    @Mapping(target = "address", expression = "java(makers.getAddress().addressToString())")
    DeliveryDto.DeliveryMakers toDeliveryMakers(Makers makers, List<DeliveryDto.DeliveryFood> deliveryFoodList, LocalTime pickupTime);

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
                .userPhone(dailyFoodDelivery.getOrderItemDailyFood().getOrder().getUser().getPhone())
                .memo(null) // 추후수정
                .build();
    }

    default List<DeliveryDto.DeliveryManifest> toDeliveryManifests(List<DailyFoodDelivery> dailyFoodDeliveries) {
        return dailyFoodDeliveries.stream()
                .map(this::toDeliveryManifest)
                .sorted(Comparator.comparing(DeliveryDto.DeliveryManifest::getServiceDate))
                .toList();
    }
}
