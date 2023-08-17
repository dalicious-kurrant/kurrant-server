package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.SupportType;
import co.dalicious.domain.order.dto.ServiceDiningVo;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.system.enums.Days;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.enums.DiningType;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserSupportPriceUtil {
    public static BigDecimal PARTIAL_NUMBER = BigDecimal.valueOf(62471004L);

    public static BigDecimal getGroupSupportPriceByDiningType(Spot spot, DiningType diningType, LocalDate serviceDay) {
        String todayOfWeek = serviceDay.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA);
        List<MealInfo> mealInfos = spot.getMealInfos();
        CorporationMealInfo mealInfo = (CorporationMealInfo) mealInfos.stream().filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO));
        List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = mealInfo.getServiceDaysAndSupportPrices();
        ServiceDaysAndSupportPrice serviceDaysAndSupportPrice = serviceDaysAndSupportPriceList.stream()
                .filter(o -> o.getSupportDays().contains(Days.ofString(todayOfWeek)))
                .findAny().orElse(null);
        return serviceDaysAndSupportPrice == null ? BigDecimal.ZERO : serviceDaysAndSupportPrice.getSupportPrice();
    }

    public static SupportType getSupportType(BigDecimal supportPrice) {
        if (supportPrice == null || supportPrice.compareTo(BigDecimal.ZERO) == 0) {
            return SupportType.NONE;
        }
        if ((supportPrice.compareTo(BigDecimal.ZERO) > 0 && supportPrice.compareTo(BigDecimal.ONE) < 1) || supportPrice.equals(PARTIAL_NUMBER)) {
            return SupportType.PARTIAL;
        }
        return SupportType.FIXED;
    }

    public static BigDecimal getUsedSupportPrice(Spot spot, List<DailyFoodSupportPrice> userSupportPriceHistories, LocalDate serviceDate, DiningType diningType) {
        // TODO: 정기식사 결제 상품만 계산
        BigDecimal usedSupportPrice = BigDecimal.ZERO;
        for (DailyFoodSupportPrice dailyFoodSupportPrice : userSupportPriceHistories) {
            if (dailyFoodSupportPrice.getGroup().equals(spot.getGroup()) && dailyFoodSupportPrice.getServiceDate().equals(serviceDate) && dailyFoodSupportPrice.getDiningType().equals(diningType)) {
                if (dailyFoodSupportPrice.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                    usedSupportPrice = usedSupportPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
                }
            }
        }
        return usedSupportPrice;
    }

    public static BigDecimal getUsedSupportPrice(List<DailyFoodSupportPrice> userSupportPriceHistories) {
        BigDecimal usedSupportPrice = BigDecimal.ZERO;
        for (DailyFoodSupportPrice dailyFoodSupportPrice : userSupportPriceHistories) {
            if (dailyFoodSupportPrice.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                usedSupportPrice = usedSupportPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
            }
        }
        // 추후 수정
        return usedSupportPrice;
    }

    public static PeriodDto getEarliestAndLatestServiceDate(Set<ServiceDiningVo> serviceDiningVos) {
        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
        LocalDate earliestServiceDate = serviceDiningVos.stream()
                .min(Comparator.comparing(ServiceDiningVo::getServiceDate))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND)).getServiceDate();

        LocalDate latestServiceDate = serviceDiningVos.stream()
                .max(Comparator.comparing(ServiceDiningVo::getServiceDate))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND)).getServiceDate();
        return PeriodDto.builder()
                .startDate(earliestServiceDate)
                .endDate(latestServiceDate)
                .build();
    }

    public static BigDecimal getUsableSupportPrice(BigDecimal itemPrice, BigDecimal usableSupportPrice) {
        if (itemPrice.compareTo(usableSupportPrice) <= 0) {
            return itemPrice;
        } else {
            return usableSupportPrice;
        }
    }

    public static BigDecimal getUsableSupportPrice(Spot spot, List<DailyFoodSupportPrice> userSupportPriceHistories, LocalDate serviceDate, DiningType diningType) {
        if (spot.getGroup() instanceof Corporation) {
            BigDecimal supportPrice = getGroupSupportPriceByDiningType(spot, diningType, serviceDate);
            switch (getSupportType(supportPrice)) {
                case NONE -> {
                    return null;
                }
                case FIXED -> {
                    BigDecimal usedSupportPrice = getUsedSupportPrice(spot, userSupportPriceHistories, serviceDate, diningType);
                    return supportPrice.subtract(usedSupportPrice);
                }
                case PARTIAL -> {
                    return supportPrice;
                }
            }
        }
        return null;
    }
}
