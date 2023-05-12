package co.dalicious.domain.paycheck.mapper;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.PrepaidCategory;
import co.dalicious.domain.client.entity.enums.PaycheckCategoryItem;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.*;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.util.PaycheckUtils;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

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
                .excelFile(corporationPaycheck.getExcelFile() == null ? null : corporationPaycheck.getExcelFile().getLocation())
                .pdfFile(corporationPaycheck.getPdfFile() == null ? null : corporationPaycheck.getPdfFile().getLocation())
                .build();
    }

    default List<PaycheckDto.CorporationResponse> toDtos(List<CorporationPaycheck> corporationPaychecks) {
        return corporationPaychecks.stream()
                .map(this::toDto)
                .toList();
    }

    ;

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

    default ExpectedPaycheck toExpectedPaycheck(Corporation corporation, CorporationPaycheck corporationPaycheck) {
        YearMonth yearMonth = YearMonth.now();
        List<PaycheckCategory> paycheckCategories = toPaycheckCategories(corporation.getPrepaidCategories());
        return (paycheckCategories == null || paycheckCategories.isEmpty()) ? null : new ExpectedPaycheck(yearMonth, paycheckCategories, corporationPaycheck);
    }

    default PaycheckCategory toPaycheckCategory(PrepaidCategory prepaidCategory, Integer days) {
        YearMonth yearMonth = YearMonth.now();
        return new PaycheckCategory(prepaidCategory.getPaycheckCategoryItem(), days, prepaidCategory.getCount(), prepaidCategory.getPrice(), prepaidCategory.getTotalPrice());
    }

    default List<PaycheckCategory> toPaycheckCategories(List<PrepaidCategory> prepaidCategories) {
        // TODO: 일수 추가 필요
        return prepaidCategories.stream()
                .map(v -> this.toPaycheckCategory(v, 0))
                .toList();
    }

    default PaycheckDto.CorporationMain toListDto(List<CorporationPaycheck> corporationPaychecks) {
        Map<PaycheckStatus, Integer> statusMap = new HashMap<>();
        List<PaycheckDto.CorporationResponse> corporationLists = new ArrayList<>();
        List<PaycheckDto.StatusList> statusLists = new ArrayList<>();
        for (CorporationPaycheck corporationPaycheck : corporationPaychecks) {
            corporationLists.add(toDto(corporationPaycheck));
            if(statusMap.containsKey(corporationPaycheck.getPaycheckStatus())) {
                Integer count = statusMap.get(corporationPaycheck.getPaycheckStatus());
                statusMap.put(corporationPaycheck.getPaycheckStatus(), ++count);
                continue;
            }
            statusMap.put(corporationPaycheck.getPaycheckStatus(), 1);
        }
        for (PaycheckStatus paycheckStatus : statusMap.keySet()) {
            PaycheckDto.StatusList statusList = new PaycheckDto.StatusList(paycheckStatus.getPaycheckStatus(), statusMap.get(paycheckStatus));
            statusLists.add(statusList);
        }
        return new PaycheckDto.CorporationMain(corporationLists, statusLists);
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

        // 주문 그룹이 메드트로닉일 경우
        if(dailyFoodSupportPrice.getGroup().getId().equals(BigInteger.valueOf(97))) {
            BigDecimal totalSupportPrice = BigDecimal.ZERO;
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
                // 취소된 상품은 제외
                if(!OrderStatus.completePayment().contains(orderItemDailyFood.getOrderStatus())) continue;
                BigDecimal supportPricePerItem = orderItemDailyFood.getOrderItemTotalPrice().multiply(BigDecimal.valueOf(0.5));
                totalSupportPrice = totalSupportPrice.add(supportPricePerItem);
                corporationOrderItems.add(new PaycheckDto.CorporationOrderItem(orderItemDailyFood, supportPricePerItem));
            }
            if(totalSupportPrice.compareTo(supportPrice) != 0) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_SUPPORT_PRICE);
            }
            return corporationOrderItems;
        }


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

    // FIXME: dailyFoodSupportPrices -> order의 PaymentType은 1.
    default List<PaycheckCategory> toPaycheckDailyFood(List<DailyFoodSupportPrice> dailyFoodSupportPrices) {
        List<PaycheckCategory> paycheckCategories = new ArrayList<>();
        Integer morningCount = 0;
        Integer lunchCount = 0;
        Integer dinnerCount = 0;

        BigDecimal morningPrice = BigDecimal.ZERO;
        BigDecimal lunchPrice = BigDecimal.ZERO;
        BigDecimal dinnerPrice = BigDecimal.ZERO;

        for (DailyFoodSupportPrice dailyFoodSupportPrice : dailyFoodSupportPrices) {
            switch (dailyFoodSupportPrice.getDiningType()) {
                case MORNING -> {
                    morningCount += dailyFoodSupportPrice.getCount();
                    morningPrice = morningPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
                }
                case LUNCH -> {
                    lunchCount += dailyFoodSupportPrice.getCount();
                    lunchPrice = lunchPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
                }
                case DINNER -> {
                    dinnerCount += dailyFoodSupportPrice.getCount();
                    dinnerPrice = dinnerPrice.add(dailyFoodSupportPrice.getUsingSupportPrice());
                }
            }
        }

        if (morningCount != 0 && morningPrice.compareTo(BigDecimal.ZERO) != 0) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.BREAKFAST, null, morningCount, null, morningPrice));
        }
        if (lunchCount != 0 && lunchPrice.compareTo(BigDecimal.ZERO) != 0) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.LUNCH, null, lunchCount, null, lunchPrice));
        }
        if (dinnerCount != 0 && dinnerPrice.compareTo(BigDecimal.ZERO) != 0) {
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.DINNER, null, dinnerCount, null, dinnerPrice));
        }
        return paycheckCategories;
    }

    default List<PaycheckAdd> toPaycheckAdds(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .map(this::toPaycheckAdd)
                .toList();
    }

    @Mapping(source = "issueDate", target = "issueDate", qualifiedByName = "stringToLocalDate")
    PaycheckAdd toPaycheckAdd(PaycheckDto.PaycheckAddDto paycheckAddDto);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String localDate) {
        return DateUtils.stringToDate(localDate);
    }

    default List<PaycheckAdd> toMemoPaycheckAdds(List<PaycheckDto.PaycheckAddDto> paycheckAddDto) {
        return paycheckAddDto.stream()
                .map(this::toPaycheckAdd)
                .toList();
    }

    @Mapping(target = "issueDate", expression = "java(DateUtils.format(paycheckAdd.getIssueDate()))")
    PaycheckDto.PaycheckAddDto toPaycheckAddDto(PaycheckAdd paycheckAdd);

    default List<PaycheckDto.PaycheckAddDto> toPaycheckAddDtos(List<PaycheckAdd> paycheckAdds) {
        return paycheckAdds.stream().map(this::toPaycheckAddDto).toList();
    }

//    default List<PaycheckCategory> toMembership(Integer membershipSupportPrices) {
//        List<PaycheckCategory> paycheckCategories = new ArrayList<>();
//        // FIXME: 멤버십 가격 10000원?
//        BigDecimal membershipPrice = BigDecimal.valueOf(10000);
//        BigDecimal totalPrice = membershipPrice.multiply(BigDecimal.valueOf(membershipSupportPrices));
//
//        paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.MEMBERSHIP, null, membershipSupportPrices, membershipPrice, totalPrice));
//
//        return paycheckCategories;
//    }
    default List<PaycheckCategory> toMembership(List<MembershipSupportPrice> membershipSupportPrices) {
        List<PaycheckCategory> paycheckCategories = new ArrayList<>();
        // 중간에 멤버십 가격이 변동될 수 있으므로 금액을 구분해서 확인
        MultiValueMap<BigDecimal, MembershipSupportPrice> membershipMap = new LinkedMultiValueMap<>();
        for (MembershipSupportPrice membershipSupportPrice : membershipSupportPrices) {
            membershipMap.add(membershipSupportPrice.getUsingSupportPrice(), membershipSupportPrice);
        }

        for (BigDecimal bigDecimal : membershipMap.keySet()) {
            BigDecimal totalPrice = bigDecimal.multiply(BigDecimal.valueOf(membershipMap.get(bigDecimal).size()));
            paycheckCategories.add(new PaycheckCategory(PaycheckCategoryItem.MEMBERSHIP, null, membershipMap.get(bigDecimal).size(), bigDecimal, totalPrice));
        }
        return paycheckCategories;
    }

    @Mapping(source = "createdDateTime", target = "issueDate")
    @Mapping(source = "discountedPrice", target = "price")
    @Mapping(source = "usage", target = "memo")
    PaycheckAdd toPaycheckAdd(OrderItemDailyFood orderItemDailyFood);

    default List<PaycheckCategory> toPaycheckCategories(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices) {
        return PaycheckUtils.getAdditionalPaycheckCategories(corporation, dailyFoodSupportPrices);
    }

    default CorporationPaycheck toInitiateEntity(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices, List<MembershipSupportPrice> membershipSupportPrices, YearMonth yearMonth) {
//    default CorporationPaycheck toInitiateEntity(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Integer membershipSupportPrices) {
        // 1. 정기 식사 구매 계산
        List<PaycheckCategory> paycheckCategories = toPaycheckDailyFood(dailyFoodSupportPrices);
        // 2. 멤버십 계산
        List<PaycheckCategory> memberships = (membershipSupportPrices == null) ? null : toMembership(membershipSupportPrices);
        // 3. 추가 이슈
        List<PaycheckCategory> paycheckCategories1 = toPaycheckCategories(corporation, dailyFoodSupportPrices);

        List<PaycheckCategory> addedPaycheckCategories = new ArrayList<>(paycheckCategories);
        if (memberships != null) addedPaycheckCategories.addAll(memberships);
        if (paycheckCategories1 != null) addedPaycheckCategories.addAll(paycheckCategories1);

        return CorporationPaycheck.builder()
                .yearMonth(yearMonth)
                .paycheckStatus(PaycheckStatus.REGISTER)
                .managerName(null)
                .phone(null)
                .paycheckCategories(addedPaycheckCategories)
                .corporation(corporation)
                .build();
    }

    @Mapping(source = "paycheckCategoryItem.paycheckCategoryItem", target = "category")
    @Mapping(target = "price", expression = "java(paycheckCategory.getPrice() == null ? null : paycheckCategory.getPrice().intValue())")
    @Mapping(target = "totalPrice", expression = "java(paycheckCategory.getTotalPrice() == null ? null : paycheckCategory.getTotalPrice().intValue())")
    PaycheckDto.PaycheckCategory toPaycheckCategoryDto(PaycheckCategory paycheckCategory);

    default List<PaycheckDto.PaycheckCategory> toPaycheckCategoryDtos(List<PaycheckCategory> paycheckCategories, Integer days) {
        return paycheckCategories.stream()
                .map(v -> (PaycheckDto.PaycheckCategory) this.toPaycheckCategoryDto(v))
                .toList();
    }

    @Mapping(source = "paycheckCategoryItem.paycheckCategoryItem", target = "category")
    @Mapping(target = "price", expression = "java(paycheckCategory.getPrice() == null ? null : paycheckCategory.getPrice().intValue())")
    @Mapping(source = "days", target = "days")
    @Mapping(target = "totalPrice", expression = "java(paycheckCategory.getTotalPrice() == null ? null : paycheckCategory.getTotalPrice().intValue())")
    PaycheckDto.PaycheckCategory toExpectedPaycheckCategoryDto(PaycheckCategory paycheckCategory);

    default List<PaycheckDto.PaycheckCategory> toExpectedPaycheckCategoryDtos(List<PaycheckCategory> paycheckCategories, Integer days) {
        if (paycheckCategories == null || paycheckCategories.isEmpty()) {
            return null;
        }

        return paycheckCategories.stream()
                .map(v -> (PaycheckDto.PaycheckCategory) this.toExpectedPaycheckCategoryDto(v))
                .toList();
    }

    default List<PaycheckDto.MemoResDto> toMemoDtos(List<PaycheckMemo> paycheckMemos) {
        return paycheckMemos.stream()
                .map(this::toMemoDto)
                .toList();
    }

    @Mapping(target = "createdDateTime", expression = "java(DateUtils.format(paycheckMemo.getCreatedDateTime()))")
    PaycheckDto.MemoResDto toMemoDto(PaycheckMemo paycheckMemo);

    default PaycheckDto.Invoice toInvoice(CorporationPaycheck corporationPaycheck, Integer days) {
        PaycheckDto.Invoice invoice = new PaycheckDto.Invoice();
        BigDecimal prepaidPrice = corporationPaycheck.getPrepaidTotalPrice();
        BigDecimal totalPrice = corporationPaycheck.getTotalPrice();
        BigDecimal vatTotalPrice = totalPrice.subtract(prepaidPrice == null ? BigDecimal.ZERO : prepaidPrice).multiply(BigDecimal.valueOf(1.1));

        invoice.setCorporationResponse(toDto(corporationPaycheck));
        invoice.setPrepaidPaycheck(toExpectedPaycheckCategoryDtos(corporationPaycheck.getExpectedPaycheck() == null ? null : corporationPaycheck.getExpectedPaycheck().getPaycheckCategories(), days));
        invoice.setPaycheck(toPaycheckCategoryDtos(corporationPaycheck.getPaycheckCategories(), days));
        invoice.setPaycheckAdds(toPaycheckAddDtos(corporationPaycheck.getPaycheckAdds()));
        invoice.setMemoResDtos(toMemoDtos(corporationPaycheck.getPaycheckMemos()));
        invoice.setPrepaidTotalPrice(prepaidPrice == null ? null : prepaidPrice.intValue());
        invoice.setTotalPrice(totalPrice.intValue());
        invoice.setVatTotalPrice(vatTotalPrice.intValue());
        return invoice;
    }
}
