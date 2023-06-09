package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.dto.SpotInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.dto.delivery.ServiceDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

//    default DeliveryDto toDeliveryDto(List<Group> groupList, List<Spot> spotList, List<DailyFood> dailyFoodList, List<OrderItemDailyFood> orderItemDailyFoodList) {
//        DeliveryDto deliveryDto = new DeliveryDto();
//
//        deliveryDto.setGroupInfoList(groupList.stream().map(this::toGroupInfo).toList());
//        deliveryDto.setSpotInfoList(spotList.stream().map(this::toSpotInfo).toList());
//        deliveryDto.setDeliveryInfoList(toDeliveryInfo(dailyFoodList, orderItemDailyFoodList));
//
//        return deliveryDto;
//    }

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    GroupInfo toGroupInfo(Group group);

    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "spot.name", target = "spotName")
    SpotInfo toSpotInfo(Spot spot);

//    default List<DeliveryDto.DeliveryInfo> toDeliveryInfo(List<DailyFood> dailyFoodList, List<OrderItemDailyFood> orderItemDailyFoodList) {
//        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();
//
//        MultiValueMap<LocalDate, DailyFood> serviceDateMap = new LinkedMultiValueMap<>();
//
//        for(DailyFood dailyFood : dailyFoodList) {
//            serviceDateMap.add(dailyFood.getServiceDate(), dailyFood);
//        }
//
//        for(LocalDate serviceDate : serviceDateMap.keySet()) {
//
//        }
//
//
//        return deliveryInfoList;
//    }

    @Mapping(source = "serviceDateDto.serviceDate", target = "serviceDate")
    @Mapping(source = "serviceDateDto.deliveryTime", target = "deliveryTime")
    @Mapping(source = "deliveryGroupList", target = "group")
    DeliveryDto.DeliveryInfo toDeliveryInfo(ServiceDateDto serviceDateDto, List<DeliveryDto.DeliveryGroup> deliveryGroupList);

    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(target = "address", expression = "java(spot.getAddress().addressToString())")
    @Mapping(source = "deliveryMakersList", target = "makers")
    DeliveryDto.DeliveryGroup toDeliveryGroup(Spot spot, Integer diningType, List<DeliveryDto.DeliveryMakers> deliveryMakersList);

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
    @Mapping(source = "pickupTime", target = "pickupTime")
    @Mapping(source = "deliveryFoodList", target = "foods")
    @Mapping(target = "address", expression = "java(makers.getAddress().addressToString())")
    DeliveryDto.DeliveryMakers toDeliveryMakers(Makers makers, List<DeliveryDto.DeliveryFood> deliveryFoodList, LocalTime pickupTime);

}
