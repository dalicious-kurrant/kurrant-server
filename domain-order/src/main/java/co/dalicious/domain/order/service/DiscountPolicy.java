package co.dalicious.domain.order.service;

import co.dalicious.domain.order.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface DiscountPolicy {
    // 총 할인 금액
    BigDecimal orderTotalPrice(List<OrderItem> orderItemList);
    // 상품별 총 할인 금액
    BigDecimal orderItemTotalPrice(OrderItem orderItem);
    // 정률 할인
    BigDecimal discountByRate(BigDecimal price, Integer discountRate);
    // 포인트 할인
    BigDecimal discountByPoint(BigDecimal price, BigDecimal point);
}