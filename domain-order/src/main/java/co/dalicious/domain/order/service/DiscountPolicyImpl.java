package co.dalicious.domain.order.service;

import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DiscountPolicyImpl implements DiscountPolicy {

    @Override
    public BigDecimal orderTotalPrice(List<OrderItem> orderItemList) {
        return null;
    }

    @Override
    public BigDecimal orderItemTotalPrice(OrderItem orderItem) {
        // DailyFood 일 경우
        if (orderItem instanceof OrderDailyFood) {
            List<FoodDiscountPolicy> foodDiscountPolicyList = ((OrderDailyFood) orderItem).getFood().getFoodDiscountPolicyList();
            BigDecimal foodPrice = ((OrderDailyFood) orderItem).getFood().getPrice();
            BigDecimal totalPrice;
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
            totalPrice = discountedTotalPrice(foodPrice, membershipDiscountPolicyRate);
            // 2. 판매자 할인 적용
            totalPrice = discountedTotalPrice(foodPrice, makersDiscountPolicyRate);
            // 3. 기간 할인 적용
            totalPrice = discountedTotalPrice(foodPrice, periodDiscountPolicyRate);
            // 개수 곱하기
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(count));

            return totalPrice;
        }
        // Membership일 경우
        if (orderItem instanceof OrderMembership) {
            if (((OrderMembership) orderItem).getMembership() != null) {
                List<MembershipDiscountPolicy> membershipDiscountPolicyList = ((OrderMembership) orderItem).getMembership().getMembershipDiscountPolicyList();
                BigDecimal membershipPrice = ((OrderMembership) orderItem).getMembership().getMembershipSubscriptionType().getPrice();
                BigDecimal totalPrice;
                Integer yearSubscriptionDiscountPolicyRate = 0;
                Integer periodDiscountPolicyRate = 0;
                for (MembershipDiscountPolicy membershipDiscountPolicy : membershipDiscountPolicyList) {
                    switch (membershipDiscountPolicy.getDiscountType()) {
                        case YEAR_DESCRIPTION_DISCOUNT ->
                                yearSubscriptionDiscountPolicyRate = membershipDiscountPolicy.getDiscountRate();
                        case PERIOD_DISCOUNT -> periodDiscountPolicyRate = membershipDiscountPolicy.getDiscountRate();
                    }
                }
                // 1. 연간 구독 할인 적용
                totalPrice = discountedTotalPrice(membershipPrice, yearSubscriptionDiscountPolicyRate);
                // 2. 기간 할인 적용
                totalPrice = discountedTotalPrice(totalPrice, periodDiscountPolicyRate);
                return totalPrice;
            }
            throw new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND);
        }
        List<MembershipDiscountPolicy> membershipDiscountPolicyList = ((OrderMembership) orderItem).getMembership().getMembershipDiscountPolicyList();
        return ((OrderMembership) orderItem).getMembership().getMembershipSubscriptionType().getPrice();
    }

    public static BigDecimal discountedTotalPrice(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf((100 - discountRate) / 100));
    }

    public static BigDecimal discountedPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf(discountRate / 100));
    }

    public static BigDecimal discountByPoint(BigDecimal price, BigDecimal point) {
        return price.subtract(point);
    }

}
