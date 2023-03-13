package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.kurrant.app.admin_api.dto.DeliveryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {


    @Mapping(source = "orderItemDailyFood.dailyFood.serviceDate", target = "serviceDate")
    DeliveryDto.DeliveryInfo toDeliveryInfo(OrderItemDailyFood orderItemDailyFood);

    @Mapping(source = "", target = "foodId")
    @Mapping(source = "", target = "foodName")
    @Mapping(source = "", target = "foodCount")
    DeliveryDto.DeliveryFood toDeliveryFood(OrderItemDailyFood orderItemDailyFood);

}
