package co.dalicious.domain.food.util;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.Food;

import java.math.BigDecimal;

public class FoodUtil {
    public static BigDecimal getFoodTotalDiscountedPrice(Food food, DiscountDto discountDto) {
        BigDecimal foodPrice = food.getPrice();
        BigDecimal totalPrice;

        Integer membershipDiscountPolicyRate = discountDto.getMembershipDiscountRate();
        Integer makersDiscountPolicyRate = discountDto.getMakersDiscountRate();
        Integer periodDiscountPolicyRate = discountDto.getPeriodDiscountRate();

        // 1. 멤버십 할인 적용
        totalPrice = discountedPriceByRate(foodPrice, membershipDiscountPolicyRate);
        // 2. 판매자 할인 적용
        totalPrice = discountedPriceByRate(totalPrice, makersDiscountPolicyRate);
        // 3. 기간 할인 적용
        totalPrice = discountedPriceByRate(totalPrice, periodDiscountPolicyRate);

        return totalPrice;
    }

    public static BigDecimal discountedPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf((100.0 - discountRate) / 100));
    }
}
