package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckDailyFood;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;
import co.dalicious.domain.paycheck.mapper.CorporationPaycheckMapper;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.MakersPaycheckRepository;
import co.dalicious.domain.paycheck.service.ExcelService;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    public List<MakersPaycheck> generateAllMakersPaycheck(List<PaycheckDto.PaycheckDailyFood> paycheckDailyFoodDtos) {
        MultiValueMap<Makers, PaycheckDailyFood> paycheckDailyFoodMap = new LinkedMultiValueMap<>();
        for (PaycheckDto.PaycheckDailyFood paycheckDailyFoodDto : paycheckDailyFoodDtos) {
            PaycheckDailyFood paycheckDailyFood = makersPaycheckMapper.toPaycheckDailyFood(paycheckDailyFoodDto);
            paycheckDailyFoodMap.add(paycheckDailyFoodDto.getMakers(), paycheckDailyFood);
        }
        for (Makers makers : paycheckDailyFoodMap.keySet()) {
            // 메이커스 정산 생성 및 저장
            MakersPaycheck makersPaycheck = makersPaycheckMapper.toInitiateEntity(paycheckDailyFoodMap.get(makers), makers);
            makersPaycheck = makersPaycheckRepository.save(makersPaycheck);
            // 정산 엑셀 생성
            ImageResponseDto imageResponseDto = excelService.createMakersPaycheckExcel(makersPaycheck);
            Image excelFile = new Image(imageResponseDto);
            makersPaycheck.updateExcelFile(excelFile);
        }
        return makersPaycheckRepository.findAllByYearMonth(YearMonth.now());
    }

    @Override
    @Transactional
    public MakersPaycheck generateMakersPaycheck(Makers makers, List<OrderItemDailyFood> dailyFoods) {
        MultiValueMap<ServiceDiningDto, OrderItemDailyFood> serviceDiningMap = new LinkedMultiValueMap<>();
        List<PaycheckDailyFood> paycheckDailyFoods = new ArrayList<>();

        // 1. 식사 일정별로 묶기
        for (OrderItemDailyFood orderItemDailyFood : dailyFoods) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(orderItemDailyFood.getDailyFood());
            serviceDiningMap.add(serviceDiningDto, orderItemDailyFood);
        }
        for (ServiceDiningDto serviceDiningDto : serviceDiningMap.keySet()) {
            List<OrderItemDailyFood> dailyFoodListByDate = serviceDiningMap.get(serviceDiningDto);
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
                        .serviceDate(serviceDiningDto.getServiceDate())
                        .diningType(serviceDiningDto.getDiningType())
                        .name(orderItemDailyFoodsByFood.get(0).getName())
                        .supplyPrice(orderItemDailyFoodsByFood.get(0).getDailyFood().getSupplyPrice() == null ? orderItemDailyFoodsByFood.get(0).getDailyFood().getFood().getSupplyPrice() : orderItemDailyFoodsByFood.get(0).getDailyFood().getSupplyPrice())
                        .count(count)
                        .food(food)
                        .build();
                paycheckDailyFoods.add(paycheckDailyFood);
            }
        }

        paycheckDailyFoods = paycheckDailyFoods.stream().sorted(Comparator.comparing(PaycheckDailyFood::getServiceDate).thenComparing(v -> v.getDiningType().getCode())).toList();

        return makersPaycheckMapper.toInitiateEntity(makers, null, null, paycheckDailyFoods);
    }

    @Override
    public PaycheckType getPaycheckType(Corporation corporation) {
        Boolean isMembershipSupport = corporation.getIsMembershipSupport();
        Boolean isPrepaid = corporation.getIsPrepaid();

        if(!isMembershipSupport) {
            return PaycheckType.NO_MEMBERSHIP;
        }
        if(!isPrepaid) {
            return PaycheckType.POSTPAID_MEMBERSHIP;
        }
        // TODO: 예외 멤버십 선불 추가
        if(corporation.getName().contains("메드트로닉")) {
            return PaycheckType.PREPAID_MEMBERSHIP_EXCEPTION_MEDTRONIC;
        }

        return PaycheckType.PREPAID_MEMBERSHIP;
    }

    public BigDecimal getCorporationDeliveryFee(Corporation corporation) {
        // TODO: 정산시 사용, 앱에서는 0원으로 지정
        PaycheckType paycheckType = getPaycheckType(corporation);
        if (paycheckType.equals(PaycheckType.POSTPAID_MEMBERSHIP) || paycheckType.equals(PaycheckType.PREPAID_MEMBERSHIP)) {
            return deliveryFeePolicy.getMembershipCorporationDeliveryFee();
        } else if (corporation.getEmployeeCount() >= 50) {
            return deliveryFeePolicy.getNoMembershipCorporationDeliveryFeeUpper50(corporation.getAddress());
        } else if (corporation.getEmployeeCount() > 0) {
            return deliveryFeePolicy.getNoMembershipCorporationDeliveryFeeLower50();
        }
        throw new ApiException(ExceptionEnum.IS_NOT_APPROPRIATE_EMPLOYEE_COUNT);
    }
}
