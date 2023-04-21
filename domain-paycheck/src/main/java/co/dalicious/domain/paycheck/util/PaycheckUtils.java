package co.dalicious.domain.paycheck.util;

import co.dalicious.domain.client.entity.PaycheckCategory;
import co.dalicious.domain.order.dto.DailySupportPriceDto;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaycheckUtils {
    // 고객사 식수
    public static List<PaycheckCategory> getDailyFood(List<DailyFoodSupportPrice> dailyFoodSupportPrices) {
        List<PaycheckCategory> paycheckCategories = new ArrayList<>();
        Integer morningCount = 0;
        Integer lunchCount = 0;
        Integer dinnerCount = 0;

        BigDecimal morningTotalPrice = BigDecimal.ZERO;
        BigDecimal lunchTotalPrice = BigDecimal.ZERO;
        BigDecimal dinnerTotalPrice = BigDecimal.ZERO;

        for (DailyFoodSupportPrice dailyFoodSupportPrice : dailyFoodSupportPrices) {
            switch (dailyFoodSupportPrice.getDiningType()) {
                case MORNING -> {
                    List<DailySupportPriceDto> dailySupportPriceDto = dailyFoodSupportPrice.getOrderItemDailyFoodCount();
                    for (DailySupportPriceDto supportPriceDto : dailySupportPriceDto) {
                        morningCount += supportPriceDto.getCount();
                        morningTotalPrice = morningTotalPrice.add(supportPriceDto.getSupportPrice());
                    }
                }
                case LUNCH -> {
                    List<DailySupportPriceDto> dailySupportPriceDto = dailyFoodSupportPrice.getOrderItemDailyFoodCount();
                    for (DailySupportPriceDto supportPriceDto : dailySupportPriceDto) {
                        lunchCount += supportPriceDto.getCount();
                        lunchTotalPrice = morningTotalPrice.add(supportPriceDto.getSupportPrice());
                    }
                }
                case DINNER -> {
                    List<DailySupportPriceDto> dailySupportPriceDto = dailyFoodSupportPrice.getOrderItemDailyFoodCount();
                    for (DailySupportPriceDto supportPriceDto : dailySupportPriceDto) {
                        dinnerCount += supportPriceDto.getCount();
                        dinnerTotalPrice = morningTotalPrice.add(supportPriceDto.getSupportPrice());
                    }
                }
            }
        }
        return null;
    }

    // 고객사 추가 주문
    public static PaycheckCategory getDailyFoodAdd() {
        return null;
    }
    // 고객사 쓰레기 수거
    public static PaycheckCategory getGarbageCharge() {
        return null;
    }
    // 고객사 온장고 사용
    public static PaycheckCategory getHotStorageCharge() {
        return null;
    }
    // 고객사 식사 셋팅
    public static PaycheckCategory getSettingCharge() {
        return null;
    }
}
