package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.dto.SpotInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.dto.delivery.ServiceDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
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

}
