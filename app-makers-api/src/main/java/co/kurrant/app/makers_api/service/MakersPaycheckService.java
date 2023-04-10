package co.kurrant.app.makers_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface MakersPaycheckService {
    List<PaycheckDto.MakersResponse> getMakersPaychecks(SecurityUser securityUser);
    void updateMakersPaycheckStatus(SecurityUser securityUser, Integer integer, List<BigInteger> ids);
}
