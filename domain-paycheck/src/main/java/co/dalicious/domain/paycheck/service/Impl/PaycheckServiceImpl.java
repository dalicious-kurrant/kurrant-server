package co.dalicious.domain.paycheck.service.Impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;
import co.dalicious.domain.paycheck.service.PaycheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaycheckServiceImpl implements PaycheckService {
    @Override
    public TransactionInfoDefault getTransactionInfoDefault() {
        return TransactionInfoDefault.builder()
                .businessNumber("376-87-00441")
                .address("서울특별시 강남구 테헤란로 51길 21, 3층(역삼동, 상경빌딩)")
                .corporationName("달리셔스 주식회사")
                .representative("이강용")
                .business("서비스 외")
                .phone("02-897-2123")
                .faxNumber("02-2179-9614")
                .businessForm("응용소프트웨어 개발 및 공급업 외")
                .build();
    }

    @Override
    public MakersPaycheck generateMakersPaycheck(Makers makers, List<DailyFood> dailyFoods) {
        return null;
    }

    @Override
    public PaycheckType getPaycheckType(Corporation corporation) {
        return null;
    }
}
