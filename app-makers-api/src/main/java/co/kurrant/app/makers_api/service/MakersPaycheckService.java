package co.kurrant.app.makers_api.service;

import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface MakersPaycheckService {
    PaycheckDto.MakersResponse getMakersPaychecks(SecurityUser securityUser, Map<String, Object> parameters);
    PaycheckDto.MakersDetail getPaycheckDetail(SecurityUser securityUser, BigInteger paycheckId);
    void updateMakersPaycheckStatus(SecurityUser securityUser, Integer integer, List<BigInteger> ids);
    void postMemo(SecurityUser securityUser, BigInteger paycheckId, PaycheckDto.MemoDto memoDto);
}
