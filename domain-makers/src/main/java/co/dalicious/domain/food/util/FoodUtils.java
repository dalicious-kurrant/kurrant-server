package co.dalicious.domain.food.util;

import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.NumberUtils;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

public class FoodUtils {
    public static BigDecimal discountedPriceByRate(BigDecimal price, Integer discountRate) {
        return NumberUtils.roundToOneDigit(price.multiply(BigDecimal.valueOf((100.0 - discountRate) / 100.0)));
    }

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

    public static void isAbleToOrderDailyFood(DailyFood dailyFood, MealInfo mealInfo, LocalTime deliveryTime) {
        LocalDateTime lastOrderTime = LocalDateTime.of(dailyFood.getServiceDate().minusDays(mealInfo.getLastOrderTime().getDay()), mealInfo.getLastOrderTime().getTime());
        if (LocalDateTime.now().isAfter(lastOrderTime)) {
            throw new ApiException(ExceptionEnum.LAST_ORDER_TIME_PASSED);
        }

        if (!dailyFood.getDailyFoodStatus().equals(DailyFoodStatus.SALES)) {
            throw new ApiException(ExceptionEnum.SOLD_OUT);
        }

        MakersCapacity makersCapacity = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType());

        if(!(makersCapacity.getMinTime() == null && makersCapacity.getMaxTime() == null) && !DateUtils.isBetween(deliveryTime, makersCapacity.getMinTime(), makersCapacity.getMaxTime())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000012", "영업하지 않는 메이커스의 음식입니다.");
        }
    }

}
