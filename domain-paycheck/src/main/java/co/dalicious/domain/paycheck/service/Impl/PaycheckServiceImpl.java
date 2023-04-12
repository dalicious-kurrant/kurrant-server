package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckDailyFood;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.service.PaycheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaycheckServiceImpl implements PaycheckService {
    private final MakersPaycheckMapper makersPaycheckMapper;
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
        return null;
    }
}
