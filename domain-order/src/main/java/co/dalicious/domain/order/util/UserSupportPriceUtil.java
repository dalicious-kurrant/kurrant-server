package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.dto.DiningTypeServiceDate;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
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

@Component
@RequiredArgsConstructor
public class UserSupportPriceUtil {
    public BigDecimal getGroupSupportPriceByDiningType(Spot spot, DiningType diningType) {
        List<MealInfo> mealInfos = spot.getMealInfos();
        CorporationMealInfo mealInfo = (CorporationMealInfo) mealInfos.stream().filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO));
        return mealInfo.getSupportPrice();
    }
    public BigDecimal getUsedSupportPrice(List<UserSupportPriceHistory> userSupportPriceHistories, LocalDate serviceDate) {
        BigDecimal usedSupportPrice = BigDecimal.ZERO;
        for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
            if(userSupportPriceHistory.getServiceDate().equals(serviceDate)) {
                usedSupportPrice = usedSupportPrice.add(userSupportPriceHistory.getUsingSupportPrice());
            }
        }
        return usedSupportPrice;
    }
    public PeriodDto getEarliestAndLatestServiceDate(List<DiningTypeServiceDate> diningTypeServiceDates) {
        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
        LocalDate earliestServiceDate = diningTypeServiceDates.stream()
                .min(Comparator.comparing(DiningTypeServiceDate::getServiceDate))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND)).getServiceDate();

        LocalDate latestServiceDate = diningTypeServiceDates.stream()
                .max(Comparator.comparing(DiningTypeServiceDate::getServiceDate))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND)).getServiceDate();
        return PeriodDto.builder()
                .startDate(earliestServiceDate)
                .endDate(latestServiceDate)
                .build();
    }

    public static BigDecimal getUsableSupportPrice(BigDecimal itemPrice, BigDecimal usableSupportPrice) {
        if(itemPrice.compareTo(usableSupportPrice) <= 0) {
            return itemPrice;
        }
        else {
            return usableSupportPrice;
        }

    }
}
