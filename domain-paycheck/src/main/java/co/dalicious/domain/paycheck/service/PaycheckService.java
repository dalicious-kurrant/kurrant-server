package co.dalicious.domain.paycheck.service;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.paycheck.entity.enums.PaycheckType;

public interface PaycheckService {
    // 정산 구분을 확인한다.
    PaycheckType getPaycheckType(Corporation corporation);

    //
}
