package co.dalicious.domain.order.service;

import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.user.dto.PeriodDto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface PointPolicy {
    // 총 할인 금액
    BigDecimal orderTotalPrice(List<OrderItem> orderItemList);
    // 상품별 총 할인 금액
    BigDecimal orderItemTotalPrice(OrderItem orderItem);
    // 멤버십 회원에게 할인되는 금액
    BigDecimal discountByMembership(Integer discountRate, BigInteger price);
    // 메이커스가 설정한 상품 할인 금액
    BigDecimal discountByMakers(Integer discountRate, BigInteger price);
    // 기간 할인
    BigDecimal discountByPeriodEvent(Integer discountRate, BigInteger price, PeriodDto eventDate);
    // 포인트 할인
    BigDecimal discountByPoint(BigInteger price);
    // 멤버십 연간 구독 할인
    BigDecimal discountByYearlyDescription(Integer discountRate, BigInteger price);
}