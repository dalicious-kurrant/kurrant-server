package co.dalicious.domain.order;

import co.dalicious.domain.user.dto.PeriodDto;

import java.math.BigInteger;

public interface PointPolicy {
    // 멤버십 회원에게 할인되는 금액
    BigInteger discountByMembership(Integer discountRate, BigInteger price);
    // 메이커스가 설정한 상품 할인 금액
    BigInteger discountByMakers(Integer discountRate, BigInteger price);
    // 기간 할인
    BigInteger discountByPeriodEvent(Integer discountRate, BigInteger price, PeriodDto eventDate);
    // 포인트 할인
    BigInteger discountByPoint(BigInteger price);
}
