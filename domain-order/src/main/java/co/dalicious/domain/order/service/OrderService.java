package co.dalicious.domain.order.service;

import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.user.entity.User;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface OrderService {
    // 정기식사 주문을 모두 취소한다
    void cancelOrderDailyFood(OrderDailyFood orderDailyFood, User user) throws IOException, ParseException;
    // 정기식사 주문 상품 하나를 취소한다.
    void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException;

}
