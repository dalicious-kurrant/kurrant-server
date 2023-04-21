package co.dalicious.domain.paycheck.service;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;

import java.util.List;

public interface PaycheckService {
    TransactionInfoDefault getTransactionInfoDefault();
    // 월별 메이커스 정산.
    List<MakersPaycheck> generateAllMakersPaycheck(List<PaycheckDto.PaycheckDailyFood> paycheckDailyFoods);

    // 메이커스 정산 Entity를 생성한다.
    MakersPaycheck generateMakersPaycheck(Makers makers, List<OrderItemDailyFood> dailyFoods);

    // 고객사 정산 구분을 확인한다.
    PaycheckType getPaycheckType(Corporation corporation);

}
