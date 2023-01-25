package co.kurrant.app.public_api.service;

import java.time.LocalDate;

public interface OrderDailyFoodService {
    // 정기식사를 구매한다
    void orderDailyFoods();
    Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate);
}
