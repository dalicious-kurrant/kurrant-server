package co.dalicious.domain.order.service;

import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
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
        if (orderItem instanceof OrderItemDailyFood) {
            List<FoodDiscountPolicy> foodDiscountPolicyList = ((OrderItemDailyFood) orderItem).getDailyFood().getFood().getFoodDiscountPolicyList();
            BigDecimal foodPrice = ((OrderItemDailyFood) orderItem).getDailyFood().getFood().getPrice();
            BigDecimal totalPrice;
            Integer count = ((OrderItemDailyFood) orderItem).getCount();
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
            totalPrice = discountedPriceByRate(foodPrice, membershipDiscountPolicyRate);
            // 2. 판매자 할인 적용
            totalPrice = discountedPriceByRate(totalPrice, makersDiscountPolicyRate);
            // 3. 기간 할인 적용
            totalPrice = discountedPriceByRate(totalPrice, periodDiscountPolicyRate);
            // 개수 곱하기
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(count));

            return totalPrice;
        }
        // Membership일 경우
        if (orderItem instanceof OrderItemMembership) {
            Membership membership = ((OrderItemMembership) orderItem).getMembership();
            List<MembershipDiscountPolicy> membershipDiscountPolicyList = membership.getMembershipDiscountPolicyList();
            BigDecimal membershipPrice = ((OrderItemMembership) orderItem).getMembership().getMembershipSubscriptionType().getPrice();
            // 할인 정책이 존재하지 않을 경우 판매가격 return.
            if(membershipDiscountPolicyList == null || membershipDiscountPolicyList.isEmpty()) {
                if(membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.MONTH)) {
                    return membershipPrice;
                }
                else if(membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.YEAR)) {
                    return discountedPriceByRate(membershipPrice, MembershipSubscriptionType.YEAR.getDiscountRate());
                }
                throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
            }
            // 할인 정책이 존재할 경우 계산
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
            totalPrice = discountedPriceByRate(membershipPrice, yearSubscriptionDiscountPolicyRate);
            // 2. 기간 할인 적용
            totalPrice = discountedPriceByRate(totalPrice, periodDiscountPolicyRate);
            return totalPrice;
        }
        throw new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND);
    }
    public static BigDecimal discountedPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf((100.0 - discountRate) / 100));
    }

    public static BigDecimal discountPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf(discountRate / 100));
    }

    public static BigDecimal discountByPoint(BigDecimal price, BigDecimal point) {
        return price.subtract(point);
    }

}
