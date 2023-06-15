package co.kurrant.app.makers_api.service;

import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.util.Map;

public interface OrderDailyFoodService {
    OrderDailyFoodByMakersDto.ByPeriod getOrder(SecurityUser securityUser, Map<String, Object> parameter);
    OrderDailyFoodByMakersDto.ByPeriod getOrderByDelivery(SecurityUser securityUser, Map<String, Object> parameter);
}
