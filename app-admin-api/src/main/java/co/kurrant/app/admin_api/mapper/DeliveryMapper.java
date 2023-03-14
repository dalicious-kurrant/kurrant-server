package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.kurrant.app.admin_api.dto.DeliveryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {


    @Mapping(source = "serviceDate", target = "serviceDate")
    @Mapping(source = "deliveryGroupList", target = "group")
    DeliveryDto.DeliveryInfo toDeliveryInfo(LocalDate serviceDate, List<DeliveryDto.DeliveryGroup> deliveryGroupList);

    @Mapping(source = "makers.id", target = "makersId")
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(source = "", target = "pickupTime")
    @Mapping(source = "deliveryFoodList", target = "foods")
    DeliveryDto.DeliveryMakers toDeliveryMakers(Makers makers, List<DeliveryDto.DeliveryFood> deliveryFoodList);

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    @Mapping(source = "group.mealInfos", target = "deliveryTime", qualifiedByName = "getDeliveryTime")
    DeliveryDto.DeliveryGroup toDeliveryGroup(Group group);

    @Named("getDeliveryTime")
    default LocalTime getDeliveryTime(List<MealInfo> mealInfos) {
        if(mealInfos.isEmpty()) return null;
        return mealInfos.stream().map(MealInfo::getDeliveryTime).min(LocalTime::compareTo).orElse(null);
    }

    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(source = "dailyFood.food.id", target = "foodId")
    @Mapping(source = "count", target = "foodCount")
    DeliveryDto.DeliveryFood toDeliveryFood(DailyFood dailyFood, Integer count);

}
