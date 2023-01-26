package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderItemDailyFoodReqDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;

public interface OrderDailyFoodService {
    // 정기식사를 구매한다
    void orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto, BigInteger spotId);
    Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate);
}
