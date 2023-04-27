package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.order.dto.ServiceDiningDto;
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
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserSupportPriceUtil {
    public static BigDecimal getGroupSupportPriceByDiningType(Spot spot, DiningType diningType) {
        String todayOfWeek = LocalDate.now(ZoneId.of("Asia/Seoul")).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA);
        List<MealInfo> mealInfos = spot.getMealInfos();
        CorporationMealInfo mealInfo = (CorporationMealInfo) mealInfos.stream().filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO));
        List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = mealInfo.getServiceDaysAndSupportPrices();
        ServiceDaysAndSupportPrice serviceDaysAndSupportPrice = serviceDaysAndSupportPriceList.stream().filter(o -> o.getSupportDays().contains(Days.ofString(todayOfWeek))).findAny().orElse(null);
        return serviceDaysAndSupportPrice == null ? BigDecimal.ZERO : serviceDaysAndSupportPrice.getSupportPrice();
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

    public static PeriodDto getEarliestAndLatestServiceDate(Set<ServiceDiningDto> serviceDiningDtos) {
        // ServiceDate의 가장 빠른 날짜와 늦은 날짜 구하기
        LocalDate earliestServiceDate = serviceDiningDtos.stream()
                .min(Comparator.comparing(ServiceDiningDto::getServiceDate))
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND)).getServiceDate();

        LocalDate latestServiceDate = serviceDiningDtos.stream()
                .max(Comparator.comparing(ServiceDiningDto::getServiceDate))
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
        //TODO: 추후 수정
        if(!spot.getGroup().getName().contains("메드트로닉")) {
            BigDecimal supportPrice =  getGroupSupportPriceByDiningType(spot, diningType);
            BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(spot, userSupportPriceHistories, serviceDate, diningType);
            return supportPrice.subtract(usedSupportPrice);
        } else {
            return BigDecimal.valueOf(62471004L);
        }
    }
}
