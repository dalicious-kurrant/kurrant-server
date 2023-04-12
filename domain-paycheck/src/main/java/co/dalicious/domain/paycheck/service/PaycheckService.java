package co.dalicious.domain.paycheck.service;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;

import java.util.List;

public interface PaycheckService {
    // 거래명세서 기본 정보를 출력한다.
    TransactionInfoDefault getTransactionInfoDefault();

    // 메이커스 정산 Entity를 생성한다.
    MakersPaycheck generateMakersPaycheck(Makers makers, List<OrderItemDailyFood> dailyFoods);

    // 정산 구분을 확인한다.
    PaycheckType getPaycheckType(Corporation corporation);

}
