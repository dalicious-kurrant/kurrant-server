package co.dalicious.domain.delivery.mappper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.DailyFood;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryInstanceMapper {
    default DeliveryInstance toEntity(DailyFood dailyFood, Spot spot, Integer orderNumber) {
        return DeliveryInstance.builder()
                .serviceDate(dailyFood.getServiceDate())
                .diningType(dailyFood.getDiningType())
                .orderNumber(orderNumber)
                .makers(dailyFood.getFood().getMakers())
                .spot(spot)
                .build();
    }
}
