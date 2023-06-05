package co.dalicious.domain.paycheck.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.PrepaidCategory;
import co.dalicious.domain.client.entity.enums.PaycheckCategoryItem;
import co.dalicious.domain.order.dto.DailySupportPriceDto;
import co.dalicious.domain.order.dto.OrderCount;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.paycheck.entity.PaycheckCategory;
import co.dalicious.system.enums.CategoryPrice;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PaycheckUtils {
    private static final BigDecimal DELIVERY_FEE_PER_ITEM = CategoryPrice.DELIVERY_FEE_PER_ITEM.getPrice();
    private static final BigDecimal DELIVERY_FEE_BELOW_50 = CategoryPrice.DELIVERY_FEE_BELOW_50.getPrice();
    private static final BigDecimal FEE_7500 = BigDecimal.valueOf(7500);
    private static final BigDecimal FEE_15000 = BigDecimal.valueOf(15000);
    private static final BigDecimal FEE_20000 = BigDecimal.valueOf(20000);
    private static final BigDecimal GARBAGE_PER_ITEM = CategoryPrice.GARBAGE_PER_ITEM.getPrice();
    private static final BigDecimal GARBAGE_PER_BELOW_50 = CategoryPrice.GARBAGE_PER_BELOW_50.getPrice();
    private static final BigDecimal HOT_STORAGE = CategoryPrice.HOT_STORAGE.getPrice();
    private static final BigDecimal MEAL_SETTING = CategoryPrice.MEAL_SETTING.getPrice();


    // 고객사 타입
    public static PaycheckType getPaycheckType(Corporation corporation) {
        Boolean isMembershipSupport = corporation.getIsMembershipSupport();
        Boolean isPrepaid = corporation.getIsPrepaid();

        if (isMembershipSupport != null && !isMembershipSupport) {
            return PaycheckType.NO_MEMBERSHIP;
        }
        if (isPrepaid != null && !isPrepaid) {
            return PaycheckType.POSTPAID_MEMBERSHIP;
        }
        // TODO: 예외 멤버십 선불 추가
        if (corporation.getName().contains("메드트로닉")) {
            return PaycheckType.PREPAID_MEMBERSHIP_EXCEPTION_MEDTRONIC;
        }

        return PaycheckType.PREPAID_MEMBERSHIP;
    }

    // 고객사 식수
    public static List<PrepaidCategory> getDailyFood(List<DailyFoodSupportPrice> dailyFoodSupportPrices) {
        List<PrepaidCategory> paycheckCategories = new ArrayList<>();
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
    public static PrepaidCategory getDailyFoodAdd() {
        return null;
    }

    public static List<PaycheckCategory> getAdditionalPaycheckCategories(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices, OrderCount orderCount) {
        List<PaycheckCategory> paycheckCategories = new ArrayList<>();

        dailyFoodSupportPrices = dailyFoodSupportPrices.stream()
                .sorted(Comparator.comparing(DailyFoodSupportPrice::getServiceDate).thenComparing(DailyFoodSupportPrice::getDiningType)).toList();

        Map<ServiceDiningDto, Integer> serviceDiningTypeMap = new HashMap<>();
        for (DailyFoodSupportPrice dailyFoodSupportPrice : dailyFoodSupportPrices) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(dailyFoodSupportPrice.getServiceDate(), dailyFoodSupportPrice.getDiningType());
            if (serviceDiningTypeMap.containsKey(serviceDiningDto)) {
                Integer count = serviceDiningTypeMap.get(serviceDiningDto) + dailyFoodSupportPrice.getCount();
                serviceDiningTypeMap.put(serviceDiningDto, count);
            } else {
                serviceDiningTypeMap.put(serviceDiningDto, dailyFoodSupportPrice.getCount());
            }
        }

        // 배송비
        List<BigInteger> deliveryFee7500 = Arrays.asList(BigInteger.valueOf(103), BigInteger.valueOf(133));
        List<BigInteger> deliveryFee15000 = Arrays.asList(BigInteger.valueOf(95), BigInteger.valueOf(96), BigInteger.valueOf(98), BigInteger.valueOf(102), BigInteger.valueOf(101));
        List<BigInteger> deliveryFee20000 = List.of(BigInteger.valueOf(100));

        BigInteger corporationId = corporation.getId();
        Integer count = serviceDiningTypeMap.size();
        BigDecimal deliveryFee = null;

        if (deliveryFee7500.contains(corporationId)) {
            deliveryFee = FEE_7500;
        } else if (deliveryFee15000.contains(corporationId)) {
            deliveryFee = FEE_15000;
        } else if (deliveryFee20000.contains(corporationId)) {
            deliveryFee = FEE_20000;
        }

        if (deliveryFee != null) {
            BigDecimal totalPrice = deliveryFee.multiply(BigDecimal.valueOf(count));
            PaycheckCategory paycheckCategory = new PaycheckCategory(PaycheckCategoryItem.DELIVERY_FEE, count, count, deliveryFee, totalPrice);
            paycheckCategories.add(paycheckCategory);
        } else if (getPaycheckType(corporation).equals(PaycheckType.NO_MEMBERSHIP)) {
            paycheckCategories.addAll(getDeliveryFee(serviceDiningTypeMap));
        }

        // 수거비
        List<BigInteger> garbage7500 = Arrays.asList(BigInteger.valueOf(103), BigInteger.valueOf(133));
        List<BigInteger> garbage15000 = List.of(BigInteger.valueOf(95));

        Integer countForGarbage = orderCount.getCounts().size();
        BigDecimal garbageFee;

        if (corporation.getId().equals(BigInteger.valueOf(97))) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.GARBAGE, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO));
        } else if (garbage7500.contains(corporationId)) {
            garbageFee = FEE_7500;
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.GARBAGE, countForGarbage, countForGarbage, garbageFee, garbageFee.multiply(BigDecimal.valueOf(countForGarbage))));
        } else if (garbage15000.contains(corporationId)) {
            garbageFee = FEE_15000;
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.GARBAGE, countForGarbage, countForGarbage, garbageFee, garbageFee.multiply(BigDecimal.valueOf(countForGarbage))));
        } else if (corporation.getIsGarbage()) {
            paycheckCategories.addAll(getGarbageCharge(orderCount));
        }

        if(corporation.getPrepaidCategory(PaycheckCategoryItem.HOT_STORAGE) != null) {
            paycheckCategories.add(getHotStorageCharge(corporation.getPrepaidCategory(PaycheckCategoryItem.HOT_STORAGE)));
        }

        if(corporation.getPrepaidCategory(PaycheckCategoryItem.SETTING) != null) {
            paycheckCategories.add(getSettingCharge(corporation.getPrepaidCategory(PaycheckCategoryItem.SETTING)));
        }
        return paycheckCategories;
    }

    // 고객사 배송비
    public static List<PaycheckCategory> getDeliveryFee(Map<ServiceDiningDto, Integer> serviceDiningTypeMap) {
        List<PaycheckCategory> paycheckCategories = new ArrayList<>();
        BigDecimal over50TotalPrice = BigDecimal.ZERO;
        BigDecimal under50TotalPrice = BigDecimal.ZERO;

        Integer over50 = 0;
        Integer under50 = 0;
        Integer countOver50 = 0;

        for (ServiceDiningDto serviceDiningDto : serviceDiningTypeMap.keySet()) {
            Integer count = serviceDiningTypeMap.get(serviceDiningDto);
            if (count >= 50) {
                over50TotalPrice = over50TotalPrice.add(DELIVERY_FEE_PER_ITEM.multiply(BigDecimal.valueOf(count)));
                countOver50 += count;
                over50++;
            } else {
                under50TotalPrice = under50TotalPrice.add(DELIVERY_FEE_BELOW_50);
                under50++;
            }
        }
        if (over50 != 0) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.DELIVERY_FEE, over50, countOver50, DELIVERY_FEE_PER_ITEM, over50TotalPrice));
        }
        if (under50 != 0) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.DELIVERY_FEE, under50, under50, DELIVERY_FEE_BELOW_50, under50TotalPrice));
        }

        return paycheckCategories;
    }

    // 고객사 쓰레기 수거
    // FIXME: 50개 이하 쓰레기 수거는 존재하지 않음?
    public static List<PaycheckCategory> getGarbageCharge(OrderCount orderCount) {
        List<PaycheckCategory> paycheckCategories = new ArrayList<>();

        BigDecimal over50TotalPrice = BigDecimal.ZERO;
        BigDecimal under50TotalPrice = BigDecimal.ZERO;

        Integer over50 = 0;
//        Integer under50 = 0;
        Integer countOver50 = 0;
        for (OrderCount.Count countDto : orderCount.getCounts()) {
            Integer count = countDto.getCount();
//            if (count >= 50) {
//                over50TotalPrice = over50TotalPrice.add(GARBAGE_PER_ITEM.multiply(BigDecimal.valueOf(count)));
//                countOver50 += count;
//                over50++;
//            } else {
//                under50TotalPrice = under50TotalPrice.add(GARBAGE_PER_BELOW_50);
//                under50++;
//            }
            over50TotalPrice = over50TotalPrice.add(GARBAGE_PER_ITEM.multiply(BigDecimal.valueOf(count)));
            countOver50 += count;
            over50++;
        }

        if (over50 != 0) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.GARBAGE, over50, countOver50, GARBAGE_PER_ITEM, over50TotalPrice));
        }
//        if (under50 != 0) {
//            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.GARBAGE, under50, under50, GARBAGE_PER_BELOW_50, under50TotalPrice));
//        }
        return paycheckCategories;
    }

    // 고객사 온장고 사용
    public static PaycheckCategory getHotStorageCharge(PrepaidCategory prepaidCategory) {
        Integer count = prepaidCategory.getCount();
        return new PaycheckCategory(PaycheckCategoryItem.HOT_STORAGE, null, count, HOT_STORAGE, HOT_STORAGE.multiply(BigDecimal.valueOf(count)));
    }

    // 고객사 식사 셋팅
    public static PaycheckCategory getSettingCharge(PrepaidCategory prepaidCategory) {
        Integer count = prepaidCategory.getCount();
        return new PaycheckCategory(PaycheckCategoryItem.SETTING, null, count, MEAL_SETTING, MEAL_SETTING.multiply(BigDecimal.valueOf(count)));
    }
}
