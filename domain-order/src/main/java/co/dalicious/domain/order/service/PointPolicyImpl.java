package co.dalicious.domain.order.service;

import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.system.util.enums.DiscountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Component
public class PointPolicyImpl implements PointPolicy {

    @Override
    public BigDecimal orderTotalPrice(List<OrderItem> orderItemList) {
        return null;
    }

    @Override
    public BigDecimal orderItemTotalPrice(OrderItem orderItem) {
        // DailyFood 일 경우
        if(orderItem instanceof OrderDailyFood) {
            List<FoodDiscountPolicy> foodDiscountPolicyList = ((OrderDailyFood) orderItem).getFood().getFoodDiscountPolicyList();
            BigDecimal foodPrice = ((OrderDailyFood) orderItem).getFood().getPrice();
            BigDecimal totalPrice = BigDecimal.ZERO;
            Integer count = ((OrderDailyFood) orderItem).getCount();
            Integer membershipDiscountPolicyRate = 0;
            Integer makersDiscountPolicyRate = 0;
            Integer periodDiscountPolicyRate = 0;
            for (FoodDiscountPolicy foodDiscountPolicy : foodDiscountPolicyList) {
                switch (foodDiscountPolicy.getDiscountType()) {
                    case MEMBERSHIP_DISCOUNT -> membershipDiscountPolicyRate = foodDiscountPolicy.getDiscountRate();
                    case MAKERS_DISCOUNT -> makersDiscountPolicyRate = foodDiscountPolicy.getDiscountRate();
                    case PERIOD_DISCOUNT -> periodDiscountPolicyRate = foodDiscountPolicy.getDiscountRate();
                }
            }
            // 1. 멤버십 할인 적용
            totalPrice = foodPrice.multiply(BigDecimal.valueOf((100 - membershipDiscountPolicyRate) / 100));
            // 2. 판매자 할인 적용
            totalPrice = totalPrice.multiply(BigDecimal.valueOf((100 - makersDiscountPolicyRate) / 100));
            // 3. 기간 할인 적용
            totalPrice = totalPrice.multiply(BigDecimal.valueOf((100 - periodDiscountPolicyRate) / 100));
            // 개수 곱하기
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(count));

            return totalPrice;
        }
        // Membership일 경우
        if(orderItem instanceof OrderMembership) {
            List<MembershipDiscountPolicy> membershipDiscountPolicyList = ((OrderMembership) orderItem).getMembership().getMembershipDiscountPolicyList();
            BigDecimal foodPrice = ((OrderMembership) orderItem).getMembership()
            BigDecimal totalPrice = BigDecimal.ZERO;
            // 1. 연간 구독 할인 적용
            // 2. 기간 할인 적용
        }
        return null;
    }

    @Override
    public BigDecimal discountByMembership(Integer discountRate, BigInteger price) {
        return null;
    }

    @Override
    public BigDecimal discountByMakers(Integer discountRate, BigInteger price) {
        return null;
    }

    @Override
    public BigDecimal discountByPeriodEvent(Integer discountRate, BigInteger price, PeriodDto eventDate) {
        return null;
    }

    @Override
    public BigDecimal discountByPoint(BigInteger price) {
        return null;
    }

    @Override
    public BigDecimal discountByYearlyDescription(Integer discountRate, BigInteger price) {
        return null;
    }
}
