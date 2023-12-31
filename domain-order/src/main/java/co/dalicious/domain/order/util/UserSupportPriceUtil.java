package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.dto.DiningTypeServiceDateDto;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.enums.DiningType;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserSupportPriceUtil {
    public static BigDecimal getGroupSupportPriceByDiningType(Spot spot, DiningType diningType) {
        List<MealInfo> mealInfos = spot.getMealInfos();
        CorporationMealInfo mealInfo = (CorporationMealInfo) mealInfos.stream().filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO));
        return mealInfo.getSupportPrice();
    }

    public static BigDecimal getUsedSupportPrice(List<UserSupportPriceHistory> userSupportPriceHistories, LocalDate serviceDate, DiningType diningType) {
        BigDecimal usedSupportPrice = BigDecimal.ZERO;
        for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
            if (userSupportPriceHistory.getServiceDate().equals(serviceDate) && userSupportPriceHistory.getDiningType().equals(diningType)) {
                if (userSupportPriceHistory.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                    usedSupportPrice = usedSupportPrice.add(userSupportPriceHistory.getUsingSupportPrice());
                }
            }
        }
        return usedSupportPrice;
    }

    public static BigDecimal getUsedSupportPrice(List<UserSupportPriceHistory> userSupportPriceHistories) {
        BigDecimal usedSupportPrice = BigDecimal.ZERO;
        for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
            if (userSupportPriceHistory.getMonetaryStatus().equals(MonetaryStatus.DEDUCTION)) {
                usedSupportPrice = usedSupportPrice.add(userSupportPriceHistory.getUsingSupportPrice());
            }
        }
        return usedSupportPrice;
    }

    public static PeriodDto getEarliestAndLatestServiceDate(Set<DiningTypeServiceDateDto> diningTypeServiceDateDtos) {
        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
        LocalDate earliestServiceDate = diningTypeServiceDateDtos.stream()
                .min(Comparator.comparing(DiningTypeServiceDateDto::getServiceDate))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND)).getServiceDate();

        LocalDate latestServiceDate = diningTypeServiceDateDtos.stream()
                .max(Comparator.comparing(DiningTypeServiceDateDto::getServiceDate))
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
}
