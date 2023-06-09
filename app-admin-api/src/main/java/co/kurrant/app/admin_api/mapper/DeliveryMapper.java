package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.dto.SpotInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.enums.DiningType;
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
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    GroupInfo toGroupInfo(Group group);

    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "spot.name", target = "spotName")
    SpotInfo toSpotInfo(Spot spot);

    @Mapping(source = "serviceDateDto.serviceDate", target = "serviceDate")
    @Mapping(source = "deliveryGroupList", target = "group")
    DeliveryDto.DeliveryInfo toDeliveryInfo(ServiceDateDto serviceDateDto, List<DeliveryDto.DeliveryGroup> deliveryGroupList);

    @Mapping(source = "spot.group.id", target = "groupId")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(target = "address", expression = "java(spot.getAddress().addressToString())")
    DeliveryDto.DeliveryGroup toDeliveryGroup(Spot spot);

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

    default List<DeliveryDto.DeliveryInfo> toDeliveryInfo(List<OrderItemDailyFood> orderItemDailyFoods) {
        List<DeliveryDto.DeliveryInfo> deliveryInfoList = new ArrayList<>();

        // service date 묶기
        MultiValueMap<ServiceDateDto, OrderItemDailyFood> serviceDateDtoMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            ServiceDateDto serviceDateDto = new ServiceDateDto(orderItemDailyFood.getDailyFood().getServiceDate(), orderItemDailyFood.getDeliveryTime());
            serviceDateDtoMap.add(serviceDateDto, orderItemDailyFood);
        }

        for (ServiceDateDto serviceDateDto : serviceDateDtoMap.keySet()) {
            List<OrderItemDailyFood> serviceDaysOrderItemDailyFoodList = serviceDateDtoMap.get(serviceDateDto);

            // spot 묶기
            MultiValueMap<Spot, OrderItemDailyFood> spotDailyFoodMap = new LinkedMultiValueMap<>();
            for (OrderItemDailyFood orderItemDailyFood : serviceDaysOrderItemDailyFoodList) {
                if(Hibernate.unproxy(orderItemDailyFood.getOrder()) instanceof OrderDailyFood orderDailyFood) {
                    Spot spot = orderDailyFood.getSpot();
                    spotDailyFoodMap.add(spot, orderItemDailyFood);
                }
            }

            List<DeliveryDto.DeliveryGroup> deliveryGroupList = new ArrayList<>();
            for (Spot spot : spotDailyFoodMap.keySet()) {
                List<OrderItemDailyFood> spotOrderItemDailyFoodList = spotDailyFoodMap.get(spot);

                // maker 묶기
                MultiValueMap<Makers, OrderItemDailyFood> makersOrderItemDailyFoodMap = new LinkedMultiValueMap<>();
                for (OrderItemDailyFood orderItemDailyFood : spotOrderItemDailyFoodList) {
                    makersOrderItemDailyFoodMap.add(orderItemDailyFood.getDailyFood().getFood().getMakers(), orderItemDailyFood);
                }

                Map<Makers, LocalTime> makersLocalTimeMap = new HashMap<>();
                List<DeliveryDto.DeliveryMakers> deliveryMakersList = new ArrayList<>();
                for (Makers makers : makersOrderItemDailyFoodMap.keySet()) {
                    List<OrderItemDailyFood> makersOrderItemDailyFoodList = Objects.requireNonNull(makersOrderItemDailyFoodMap.get(makers)).stream().sorted(Comparator.comparing(o -> o.getDailyFood().getId())).toList();

                    Map<DailyFood, Integer> dailyFoodOrderCountMap = new HashMap<>();
                    for (OrderItemDailyFood orderItemDailyFood : makersOrderItemDailyFoodList) {
                        DailyFood dailyFood = dailyFoodOrderCountMap.keySet().stream().filter(d -> d.equals(orderItemDailyFood.getDailyFood())).findAny().orElse(null);
                        if(dailyFood == null) dailyFoodOrderCountMap.put(orderItemDailyFood.getDailyFood(), orderItemDailyFood.getCount());
                        else {
                            Integer count = dailyFoodOrderCountMap.get(dailyFood);
                            dailyFoodOrderCountMap.put(dailyFood, count + orderItemDailyFood.getCount());
                        }

                        makersLocalTimeMap.put(makers, )
                    }
                    DeliveryDto.DeliveryFood deliveryFood = toDeliveryFood(dailyFood, count);
                    deliveryFoodList.add(deliveryFood);
                    deliveryFoodList = deliveryFoodList.stream().sorted(Comparator.comparing(DeliveryDto.DeliveryFood::getFoodName)).collect(Collectors.toList());

                }
                DeliveryDto.DeliveryMakers deliveryMakers = toDeliveryMakers(makers, deliveryFoodList, pickupTime);
                deliveryMakersList.add(deliveryMakers);
                // delivery makers list 를 pickup time 으로 정렬
                deliveryMakersList = deliveryMakersList.stream()
                        .sorted(Comparator.comparing(deliveryMakers -> LocalTime.parse(deliveryMakers.getPickupTime()))).collect(Collectors.toList());

                // delivery group 만들기
                DeliveryDto.DeliveryGroup deliveryGroup = toDeliveryGroup(spot);
                deliveryGroup.setDiningType(Objects.requireNonNull(diningType).getCode());
                deliveryGroup.setDeliveryTime(DateUtils.timeToString(serviceDateDto.getDeliveryTime()));
                deliveryGroup.setMakers(deliveryMakersList);

                deliveryGroupList.add(deliveryGroup);
            }

            // delivery info 만들기
            DeliveryDto.DeliveryInfo deliveryInfo = toDeliveryInfo(serviceDateDto, deliveryGroupList);
            deliveryInfoList.add(deliveryInfo);
        }
        return deliveryInfoList;
    }
}
