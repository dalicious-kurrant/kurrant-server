package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.CorporationPaycheckMapper;
import co.dalicious.domain.paycheck.repository.CorporationPaycheckRepository;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientPaycheckService;
import co.kurrant.app.client_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientPaycheckServiceImpl implements ClientPaycheckService {
    private final UserUtil userUtil;
    private final CorporationPaycheckRepository corporationPaycheckRepository;
    private final CorporationPaycheckMapper corporationPaycheckMapper;

    @Override
    @Transactional
    public List<PaycheckDto.CorporationResponse> getCorporationPaychecks(SecurityUser securityUser) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        List<CorporationPaycheck> corporationPaychecks = corporationPaycheckRepository.findAllByCorporation(corporation);
        return corporationPaycheckMapper.toDtos(corporationPaychecks);
    }

    @Override
    @Transactional
    public void updateCorporationPaycheckStatus(SecurityUser securityUser, Integer code, List<BigInteger> ids) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        PaycheckStatus paycheckStatus = PaycheckStatus.ofCode(code);
        List<CorporationPaycheck> corporationPaychecks = corporationPaycheckRepository.findAllByCorporationAndIdIn(corporation, ids);
        for (CorporationPaycheck corporationPaycheck : corporationPaychecks) {
            if(!corporationPaycheck.getCorporation().equals(corporation)) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_GROUP);
            }
            corporationPaycheck.updatePaycheckStatus(paycheckStatus);
        }
    }
}
