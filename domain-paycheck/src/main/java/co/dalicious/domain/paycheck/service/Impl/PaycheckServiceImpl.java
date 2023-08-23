package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.OrderCount;
import co.dalicious.domain.order.dto.ServiceDiningVo;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.paycheck.dto.ExcelPdfDto;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.*;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;
import co.dalicious.domain.paycheck.mapper.CorporationPaycheckMapper;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.CorporationPaycheckRepository;
import co.dalicious.domain.paycheck.repository.ExpectedPaycheckRepository;
import co.dalicious.domain.paycheck.repository.MakersPaycheckRepository;
import co.dalicious.domain.paycheck.service.ExcelService;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.dalicious.domain.paycheck.util.PaycheckUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaycheckServiceImpl implements PaycheckService {
    private final MakersPaycheckMapper makersPaycheckMapper;
    private final MakersPaycheckRepository makersPaycheckRepository;
    private final ExcelService excelService;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final CorporationPaycheckMapper corporationPaycheckMapper;
    private final CorporationPaycheckRepository corporationPaycheckRepository;
    private final ExpectedPaycheckRepository expectedPaycheckRepository;
    private final EntityManager entityManager;

    @Override
    public TransactionInfoDefault getTransactionInfoDefault() {
        return TransactionInfoDefault.builder()
                .businessNumber("376-87-00441")
                .address1("서울특별시 강남구 테헤란로 51길 21")
                .address2("3층(역삼동, 상경빌딩)")
                .corporationName("달리셔스 주식회사")
                .representative("이강용")
                .business("서비스 외")
                .phone("02-897-2123")
                .faxNumber("02-2179-9614")
                .businessForm("응용소프트웨어 개발 및 공급업 외")
                .build();
    }

    @Override
    @Transactional
    public List<MakersPaycheck> generateAllMakersPaycheck(List<? extends PaycheckDto.PaycheckDailyFood> paycheckDailyFoodDtos, YearMonth yearMonth) {
        MultiValueMap<Makers, PaycheckDailyFood> paycheckDailyFoodMap = new LinkedMultiValueMap<>();
        for (PaycheckDto.PaycheckDailyFood paycheckDailyFoodDto : paycheckDailyFoodDtos) {
            PaycheckDailyFood paycheckDailyFood = makersPaycheckMapper.toPaycheckDailyFood(paycheckDailyFoodDto);
            paycheckDailyFoodMap.add(paycheckDailyFoodDto.getMakers(), paycheckDailyFood);
        }
        for (Makers makers : paycheckDailyFoodMap.keySet()) {
            // 메이커스 정산 생성 및 저장
            MakersPaycheck makersPaycheck = makersPaycheckMapper.toInitiateEntity(paycheckDailyFoodMap.get(makers), makers, yearMonth);
            makersPaycheck = makersPaycheckRepository.save(makersPaycheck);
            // 정산 엑셀 생성
            ExcelPdfDto excelPdfDto = excelService.createMakersPaycheckExcel(makersPaycheck);
            Image excelFile = new Image(excelPdfDto.getExcelDto());
            Image pdfFile = new Image(excelPdfDto.getPdfDto());
            makersPaycheck.updateExcelFile(excelFile);
            makersPaycheck.updatePdfFile(pdfFile);
        }
        return makersPaycheckRepository.findAllByYearMonth(yearMonth);
    }

    @Override
    @Transactional
    public MakersPaycheck generateMakersPaycheck(Makers makers, List<OrderItemDailyFood> dailyFoods) {
        MultiValueMap<ServiceDiningVo, OrderItemDailyFood> serviceDiningMap = new LinkedMultiValueMap<>();
        List<PaycheckDailyFood> paycheckDailyFoods = new ArrayList<>();

        // 1. 식사 일정별로 묶기
        for (OrderItemDailyFood orderItemDailyFood : dailyFoods) {
            ServiceDiningVo serviceDiningVo = new ServiceDiningVo(orderItemDailyFood.getDailyFood());
            serviceDiningMap.add(serviceDiningVo, orderItemDailyFood);
        }
        for (ServiceDiningVo serviceDiningVo : serviceDiningMap.keySet()) {
            List<OrderItemDailyFood> dailyFoodListByDate = serviceDiningMap.get(serviceDiningVo);
            MultiValueMap<Food, OrderItemDailyFood> foodMap = new LinkedMultiValueMap<>();

            // 2. 음식별로 묶기
            for (OrderItemDailyFood orderItemDailyFood : dailyFoodListByDate) {
                foodMap.add(orderItemDailyFood.getDailyFood().getFood(), orderItemDailyFood);
            }
            for (Food food : foodMap.keySet()) {
                List<OrderItemDailyFood> orderItemDailyFoodsByFood = foodMap.get(food);
                Integer count = orderItemDailyFoodsByFood.stream()
                        .mapToInt(OrderItemDailyFood::getCount)
                        .sum();

                // 3. 메이커스 정산 리스트에 추가
                PaycheckDailyFood paycheckDailyFood = PaycheckDailyFood.builder()
                        .serviceDate(serviceDiningVo.getServiceDate())
                        .diningType(serviceDiningVo.getDiningType())
                        .name(orderItemDailyFoodsByFood.get(0).getName())
                        .supplyPrice(orderItemDailyFoodsByFood.get(0).getDailyFood().getSupplyPrice() == null ? orderItemDailyFoodsByFood.get(0).getDailyFood().getFood().getSupplyPrice() : orderItemDailyFoodsByFood.get(0).getDailyFood().getSupplyPrice())
                        .count(count)
                        .food(food)
                        .build();
                paycheckDailyFoods.add(paycheckDailyFood);
            }
        }

        paycheckDailyFoods = paycheckDailyFoods.stream().sorted(Comparator.comparing(PaycheckDailyFood::getServiceDate).thenComparing(v -> v.getDiningType().getCode())).toList();
        return makersPaycheckRepository.save(makersPaycheckMapper.toInitiateEntity(makers, null, null, paycheckDailyFoods));
    }

    /*
     * 현재 DailyFoodSupportPrice에는
     * 1. 일반 지원금 결제
     * 2. 추가 주문
     * 이 존재한다. 정산에서는 PaymentType에 따라 구분하였지만, 식사 구매에서 더 많은 예외 상황이 생긴다면, 구분이 필요하다.
     */

//    @Override
//    @Transactional
//    public CorporationPaycheck generateCorporationPaycheck(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Integer membershipSupportPriceCount) {
//
//        CorporationPaycheck corporationPaycheck = corporationPaycheckMapper.toInitiateEntity(corporation, dailyFoodSupportPrices, membershipSupportPriceCount);
//        corporationPaycheck = corporationPaycheckRepository.save(corporationPaycheck);
//
//        // 선불 정산인 경우 체크
//        ExpectedPaycheck expectedPaycheck = corporationPaycheckMapper.toExpectedPaycheck(corporation, corporationPaycheck);
//        if(expectedPaycheck != null) expectedPaycheckRepository.save(expectedPaycheck);
//        return corporationPaycheck;
//    }

    @Override
    @Transactional
    public CorporationPaycheck generateCorporationPaycheck(Corporation corporation, List<DailyFoodSupportPrice> dailyFoodSupportPrices, List<MembershipSupportPrice> membershipSupportPrices, OrderCount orderCount, YearMonth yearMonth) {
        // 1. 매니저 계정 확인

        // 2. CorporationPaycheck 생성
        CorporationPaycheck corporationPaycheck = corporationPaycheckMapper.toInitiateEntity(corporation, dailyFoodSupportPrices, membershipSupportPrices, orderCount, yearMonth);
        corporationPaycheck = corporationPaycheckRepository.save(corporationPaycheck);

        // 선불 정산인 경우 체크
        ExpectedPaycheck expectedPaycheck = corporationPaycheckMapper.toExpectedPaycheck(corporation, corporationPaycheck);
        if (expectedPaycheck != null) {
            corporationPaycheckRepository.saveAndFlush(corporationPaycheck);
            expectedPaycheckRepository.save(expectedPaycheck);
            entityManager.refresh(corporationPaycheck);
        }
        return corporationPaycheck;
    }

    public BigDecimal getCorporationDeliveryFee(Corporation corporation) {
        PaycheckType paycheckType = PaycheckUtils.getPaycheckType(corporation);
        if (paycheckType.equals(PaycheckType.POSTPAID_MEMBERSHIP) || paycheckType.equals(PaycheckType.PREPAID_MEMBERSHIP)) {
            return deliveryFeePolicy.getMembershipCorporationDeliveryFee();
        } else if (corporation.getEmployeeCount() >= 50) {
            return deliveryFeePolicy.getNoMembershipCorporationDeliveryFeeUpper50(corporation);
        } else if (corporation.getEmployeeCount() > 0) {
            return deliveryFeePolicy.getNoMembershipCorporationDeliveryFeeLower50();
        }
        throw new ApiException(ExceptionEnum.IS_NOT_APPROPRIATE_EMPLOYEE_COUNT);
    }


}
