package co.dalicious.domain.delivery.utils;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.delivery.entity.DailyFoodDelivery;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.delivery.mappper.DeliveryInstanceMapper;
import co.dalicious.domain.delivery.repository.DailyFoodDeliveryRepository;
import co.dalicious.domain.delivery.repository.DeliveryInstanceRepository;
import co.dalicious.domain.delivery.repository.QDailyFoodDeliveryRepository;
import co.dalicious.domain.delivery.repository.QDeliveryInstanceRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeliveryUtils {
    private final QDeliveryInstanceRepository qDeliveryInstanceRepository;
    private final DeliveryInstanceMapper deliveryInstanceMapper;
    private final DeliveryInstanceRepository deliveryInstanceRepository;
    private final DailyFoodDeliveryRepository dailyFoodDeliveryRepository;
    private final QDailyFoodDeliveryRepository qDailyFoodDeliveryRepository;

    public void saveDeliveryInstance(OrderItemDailyFood orderItemDailyFood, Spot spot, User user, DailyFood dailyFood, LocalTime deliveryTime) {
        DeliveryInstance deliveryInstance = getDeliveryInstance(spot, user, dailyFood, deliveryTime);
        if(deliveryInstance == null) {
            Integer deliveryOrderNumber = getNewOrderNumber(spot, dailyFood, deliveryTime);
            deliveryInstance = deliveryInstanceMapper.toEntity(dailyFood, spot, deliveryOrderNumber, deliveryTime);
            deliveryInstanceRepository.save(deliveryInstance);
        }
        DailyFoodDelivery dailyFoodDelivery = new DailyFoodDelivery(deliveryInstance, orderItemDailyFood);
        dailyFoodDeliveryRepository.save(dailyFoodDelivery);
    }

    public DeliveryInstance getDeliveryInstance(Spot spot, User user, DailyFood dailyFood, LocalTime deliveryTime) {
        if(Hibernate.getClass(spot).equals(OpenGroupSpot.class) || Hibernate.getClass(spot).equals(MySpot.class)) {
            Optional<DailyFoodDelivery> dailyFoodDeliveryOptional =  qDailyFoodDeliveryRepository.findByFilter(user, dailyFood.getFood().getMakers(), spot, dailyFood.getServiceDate(), dailyFood.getDiningType(), deliveryTime);
            return dailyFoodDeliveryOptional.map(DailyFoodDelivery::getDeliveryInstance).orElse(null);
        }
        return qDeliveryInstanceRepository.findBy(dailyFood.getServiceDate(), dailyFood.getDiningType(), deliveryTime, dailyFood.getFood().getMakers(), spot)
                .orElse(null);
    }

    public Integer getNewOrderNumber(Spot spot, DailyFood dailyFood, LocalTime deliveryTime) {
        if(spot instanceof CorporationSpot) return null;
        Integer currentMaxNumber = qDeliveryInstanceRepository.getMaxOrderNumber(dailyFood.getServiceDate(), dailyFood.getDiningType(), deliveryTime, dailyFood.getFood().getMakers());
        return ++currentMaxNumber;
    }
}
