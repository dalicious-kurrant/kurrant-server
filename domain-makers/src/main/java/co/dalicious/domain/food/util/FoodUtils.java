package co.dalicious.domain.food.util;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.repository.QFoodRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.NumberUtils;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
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

    public static Boolean isValidDeliveryTime(Makers makers, DiningType diningType, LocalTime deliveryTime) {
        MakersCapacity makersCapacity = makers.getMakersCapacity(diningType);
        LocalTime minTime = makersCapacity.getMinTime();
        LocalTime maxTime = makersCapacity.getMaxTime();

        if(minTime == null && maxTime == null) {
            return true;
        }

        if(minTime == null) {
            return deliveryTime.isBefore(maxTime) || deliveryTime.equals(maxTime);
        }

        if(maxTime == null) {
            return deliveryTime.isAfter(minTime) || deliveryTime.equals(minTime);
        }

        return (deliveryTime.isAfter(minTime) || deliveryTime.equals(minTime))
                && (deliveryTime.isBefore(maxTime) || deliveryTime.equals(maxTime));
    }

    // 상품과 메이커스 통틀어 가장 늦은 주문 마감시간
    public static LocalDateTime getLastOrderTime(Makers makers, DiningType diningType, LocalDate serviceDate, List<FoodCapacity> foodCapacities) {
        DayAndTime makersLastOrderTime = makers.getLastOrderTime(diningType);

        Optional<LocalDateTime> latestTime = foodCapacities.stream()
                .filter(v -> v.getDiningType().equals(diningType) && v.getLastOrderTime() != null)
                .map(FoodCapacity::getLastOrderTime)
                .map(v -> v.dayAndTimeToLocalDateTime(serviceDate))
                .max(Comparator.naturalOrder());

        if (makersLastOrderTime != null) {
            LocalDateTime makersTime = makersLastOrderTime.dayAndTimeToLocalDateTime(serviceDate);
            if(latestTime.isEmpty()) return makersTime;
            latestTime = latestTime.map(latest -> latest.isAfter(makersTime) ? latest : makersTime);
        }

        return latestTime.orElse(serviceDate.atTime(DateUtils.stringToLocalTime("10:00")));
    }

    public static String getEarliestLastOrderTime(DailyFood dailyFood) {
        DayAndTime makersLastOrderTime = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime mealInfoLastOrderTime = dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime foodLastOrderTime = dailyFood.getFood().getFoodCapacity(dailyFood.getDiningType()).getLastOrderTime();

        List<DayAndTime> lastOrderTimes = Stream.of(makersLastOrderTime, mealInfoLastOrderTime, foodLastOrderTime)
                .filter(Objects::nonNull) // Exclude null values
                .toList();
        DayAndTime lastOrderTime = lastOrderTimes.stream().min(Comparator.comparing(DayAndTime::getDay).reversed().thenComparing(DayAndTime::getTime))
                .orElse(new DayAndTime(0, LocalTime.MIN));

        return lastOrderTime.dayAndTimeToStringByDate(dailyFood.getServiceDate());
    }

}
