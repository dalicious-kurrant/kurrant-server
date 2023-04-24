package co.dalicious.domain.paycheck.mapper;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.PaycheckCategory;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.ExpectedPaycheck;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, PaycheckStatus.class})
public interface CorporationPaycheckMapper {
    @Mapping(source = "corporation", target = "corporation")
    @Mapping(target = "yearMonth", expression = "java(DateUtils.toYearMonth(paycheckDto.getYear(), paycheckDto.getMonth()))")
    @Mapping(target = "paycheckStatus", expression = "java(PaycheckStatus.ofCode(paycheckDto.getPaycheckStatus()))")
//    @Mapping(source = "excelFile", target = "excelFile")
//    @Mapping(source = "pdfFile", target = "pdfFile")
    @Mapping(source = "paycheckDto.managerName", target = "managerName")
    @Mapping(source = "paycheckDto.phone", target = "phone")
    CorporationPaycheck toEntity(PaycheckDto.CorporationRequest paycheckDto, Corporation corporation, Image excelFile, Image pdfFile);

    @Mapping(target = "year", expression = "java()")
    @Mapping(target = "month", expression = "java()")
    @Mapping(source = "corporation.name", target = "corporationName")
    @Mapping(source = "paycheckStatus.paycheckStatus", target = "paycheckStatus")
    @Mapping(source = "excelFile.location", target = "excelFile")
    @Mapping(source = "pdfFile.location", target = "pdfFile")
    default PaycheckDto.CorporationResponse toDto(CorporationPaycheck corporationPaycheck) {
        return PaycheckDto.CorporationResponse.builder()
                .id(corporationPaycheck.getId())
                .year(corporationPaycheck.getYearMonth().getYear())
                .month(corporationPaycheck.getYearMonth().getMonthValue())
                .corporationName(corporationPaycheck.getCorporation().getName())
                .prepaidPrice(corporationPaycheck.getExpectedPaycheck() == null ? null : corporationPaycheck.getExpectedPaycheck().getTotalPrice().intValue())
                .price(corporationPaycheck.getTotalPrice().intValue())
                .managerName(corporationPaycheck.getManagerName())
                .phone(corporationPaycheck.getPhone())
                .paycheckStatus(corporationPaycheck.getPaycheckStatus().getPaycheckStatus())
                .hasRequest(corporationPaycheck.hasRequest())
                .excelFile(corporationPaycheck.getExcelFile().getLocation())
                .pdfFile(corporationPaycheck.getPdfFile().getLocation())
                .build();
    };

    default CorporationPaycheck toInitiateEntity(Corporation corporation, User user) {
        return CorporationPaycheck.builder()
                .yearMonth(YearMonth.now())
                .paycheckStatus(PaycheckStatus.REGISTER)
                .managerName(user.getName())
                .phone(user.getPhone())
                .paycheckCategories(null)
                .corporation(corporation)
                .build();
    }

    default ExpectedPaycheck toExpectedPaycheck(Corporation corporation) {
        YearMonth yearMonth = YearMonth.now();
        List<PaycheckCategory> paycheckCategories = corporation.getPaycheckCategories();
        return new ExpectedPaycheck(yearMonth, paycheckCategories);
    };

    default List<PaycheckDto.CorporationResponse> toDtos(List<CorporationPaycheck> corporationPaycheck) {
        return corporationPaycheck.stream()
                .map(this::toDto)
                .toList();
    }

    default PaycheckDto.CorporationOrder toCorporationOrder(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices) {
        PaycheckDto.CorporationOrder corporationOrder = new PaycheckDto.CorporationOrder();
        List<PaycheckDto.CorporationOrderItem> corporationOrderItems = toCorporationOrderItems(dailyFoodSupportPrices);
        PaycheckDto.CorporationInfo corporationInfo = toCorporationInfo(corporation, corporationOrderItems);

        corporationOrder.setCorporationInfo(corporationInfo);
        corporationOrder.setCorporationOrderItems(corporationOrderItems);
        return corporationOrder;
    }

    default PaycheckDto.CorporationInfo toCorporationInfo(Corporation corporation, List<PaycheckDto.CorporationOrderItem> corporationOrderItems) {
        Integer totalPrice = 0;

        Integer morningCount = 0;
        Integer lunchCount = 0;
        Integer dinnerCount = 0;

        // 기간 구하기
        LocalDate tempDate = DateUtils.stringToDate(corporationOrderItems.get(0).getServiceDate());
        int year = tempDate.getYear();
        int month = tempDate.getMonthValue();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        PeriodDto periodDto = new PeriodDto(startOfMonth, endOfMonth);

        for (PaycheckDto.CorporationOrderItem corporationOrderItem : corporationOrderItems) {
            totalPrice += corporationOrderItem.getSupportPrice();
            switch (DiningType.ofString(corporationOrderItem.getDiningType())) {
                case MORNING -> morningCount += corporationOrderItem.getCount();
                case LUNCH -> lunchCount += corporationOrderItem.getCount();
                case DINNER -> dinnerCount += corporationOrderItem.getCount();
            }
        }
        return PaycheckDto.CorporationInfo.builder()
                .name(corporation.getName())
                .period(periodDto.toString())
                .totalPrice(totalPrice)
                .morningCount(morningCount)
                .lunchCount(lunchCount)
                .dinnerCount(dinnerCount)
                .build();
    }

    default List<PaycheckDto.CorporationOrderItem> toCorporationOrderItems(List<DailyFoodSupportPrice> dailyFoodSupportPrices) {
        List<PaycheckDto.CorporationOrderItem> corporationOrderItems = new ArrayList<>();
        for (DailyFoodSupportPrice dailyFoodSupportPrice : dailyFoodSupportPrices) {
            corporationOrderItems.addAll(toCorporationOrderItem(dailyFoodSupportPrice));
        }
        corporationOrderItems = corporationOrderItems.stream()
                .sorted(Comparator.comparing(PaycheckDto.CorporationOrderItem::getServiceDate).thenComparing(PaycheckDto.CorporationOrderItem::getDiningType))
                .toList();
        return corporationOrderItems;
    }

    default List<PaycheckDto.CorporationOrderItem> toCorporationOrderItem(DailyFoodSupportPrice dailyFoodSupportPrice) {
        List<PaycheckDto.CorporationOrderItem> corporationOrderItems = new ArrayList<>();
        BigDecimal supportPrice = dailyFoodSupportPrice.getUsingSupportPrice();
        List<OrderItemDailyFood> orderItemDailyFoods = dailyFoodSupportPrice.getOrderItemDailyFoodGroup().getOrderDailyFoods();
        // 식단 그룹 내에서 음식을 하나만 주문했을 경우
        if (orderItemDailyFoods.size() == 1) {
            OrderItemDailyFood orderItem = orderItemDailyFoods.get(0);
            corporationOrderItems.add(new PaycheckDto.CorporationOrderItem(orderItem, supportPrice));
            return corporationOrderItems;
        }

        // 식단 그룹 내에서 음식을 2개 이상 주문했을 경우
        orderItemDailyFoods = orderItemDailyFoods.stream()
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderStatus()))
                .sorted(Comparator.comparing(OrderItemDailyFood::getDiscountedPrice).reversed())
                .toList();

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if (supportPrice.compareTo(BigDecimal.ZERO) > 0) {
                PaycheckDto.CorporationOrderItem orderItem = new PaycheckDto.CorporationOrderItem(orderItemDailyFood, supportPrice);
                corporationOrderItems.add(orderItem);
                supportPrice = supportPrice.subtract(orderItemDailyFood.getDiscountedPrice());
            }
        }
        return corporationOrderItems;
    }
}
