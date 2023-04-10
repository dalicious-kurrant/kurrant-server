package co.kurrant.app.client_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.client_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface ClientPaycheckService {
    List<PaycheckDto.CorporationResponse> getCorporationPaychecks(SecurityUser securityUser);
    void updateCorporationPaycheckStatus(SecurityUser securityUser, Integer code, List<BigInteger> ids);
}
