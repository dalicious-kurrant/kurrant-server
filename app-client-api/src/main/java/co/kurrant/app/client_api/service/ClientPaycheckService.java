package co.kurrant.app.client_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.client_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface ClientPaycheckService {
    List<PaycheckDto.CorporationResponse> getCorporationPaychecks(SecurityUser securityUser, Map<String, Object> parameters);
    PaycheckDto.CorporationOrder getPaycheckOrders(SecurityUser securityUser, BigInteger paycheckId);
    PaycheckDto.Invoice getPaycheckInvoice(SecurityUser securityUser, BigInteger corporationPaycheckId);
    void updateCorporationPaycheckStatus(SecurityUser securityUser, Integer code, List<BigInteger> ids);
    void completeCorporationPaycheckStatus(SecurityUser securityUser, BigInteger id);
    void postMemo(SecurityUser securityUser, BigInteger paycheckId, PaycheckDto.MemoDto memoDto);
}
