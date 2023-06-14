package co.dalicious.domain.delivery.utils;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.mappper.DeliveryInstanceMapper;
import co.dalicious.domain.delivery.repository.DailyFoodDeliveryRepository;
import co.dalicious.domain.delivery.repository.DeliveryInstanceRepository;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DeliveryUtils {
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final DeliveryInstanceMapper deliveryInstanceMapper;
    private final DeliveryInstanceRepository deliveryInstanceRepository;
    private final DailyFoodDeliveryRepository dailyFoodDeliveryRepository;

    public void saveDeliveryInstance(OrderItemDailyFood orderItemDailyFood, Spot spot, DailyFood dailyFood, LocalTime deliveryTime) {
        DeliveryInstance deliveryInstance = getDeliveryInstance(spot, dailyFood, deliveryTime);
        if(deliveryInstance == null) {
            Integer deliveryOrderNumber = getNewOrderNumber(dailyFood, deliveryTime);
            deliveryInstance = deliveryInstanceMapper.toEntity(dailyFood, spot, deliveryOrderNumber);
            deliveryInstanceRepository.save(deliveryInstance);
        }
        DailyFoodDelivery dailyFoodDelivery = new DailyFoodDelivery(deliveryInstance, orderItemDailyFood);
        dailyFoodDeliveryRepository.save(dailyFoodDelivery);
    }

    public DeliveryInstance getDeliveryInstance(Spot spot, DailyFood dailyFood, LocalTime deliveryTime) {
        return qDeliveryInstanceRepository.findBy(dailyFood.getServiceDate(), dailyFood.getDiningType(), deliveryTime, dailyFood.getFood().getMakers(), spot)
                .orElse(null);

    }

    public Integer getNewOrderNumber(DailyFood dailyFood, LocalTime deliveryTime) {
        Integer currentMaxNumber = qDeliveryInstanceRepository.getMaxOrderNumber(dailyFood.getServiceDate(), dailyFood.getDiningType(), deliveryTime, dailyFood.getFood().getMakers());
        return ++currentMaxNumber;
    }
}
