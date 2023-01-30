package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderDailyFoodDto;
import co.dalicious.domain.order.dto.OrderDetailDto;
import co.dalicious.domain.order.dto.OrderItemDailyFoodReqDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface OrderDailyFoodService {
    // 정기식사를 구매한다
    void orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto, BigInteger spotId);
    // 식사 일정을 조회한다.
    List<OrderDetailDto> findOrderByServiceDate(SecurityUser securityUser, LocalDate startDate, LocalDate endDate);
    // 구매 내역을 조회한다.
    List<OrderDailyFoodDto> findUserOrderDailyFoodHistory(SecurityUser securityUser);
}
