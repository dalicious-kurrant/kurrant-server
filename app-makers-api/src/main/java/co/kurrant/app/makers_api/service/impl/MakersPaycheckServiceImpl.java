package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.MakersPaycheckRepository;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersPaycheckService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MakersPaycheckServiceImpl implements MakersPaycheckService {
    private final MakersPaycheckMapper makersPaycheckMapper;
    private final MakersPaycheckRepository makersPaycheckRepository;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public List<PaycheckDto.MakersResponse> getMakersPaychecks(SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);
        List<MakersPaycheck> makersPaychecks = makersPaycheckRepository.findAllByMakers(makers);
        return makersPaycheckMapper.toDtos(makersPaychecks);
    }

    @Override
    @Transactional
    public void updateMakersPaycheckStatus(SecurityUser securityUser, Integer integer, List<BigInteger> ids) {
        Makers makers = userUtil.getMakers(securityUser);
        List<MakersPaycheck> makersPaychecks = makersPaycheckRepository.findAllByMakersAndIdIn(makers, ids);
        PaycheckStatus paycheckStatus = PaycheckStatus.ofCode(integer);

        for (MakersPaycheck makersPaycheck : makersPaychecks) {
            if(!makersPaycheck.getMakers().equals(makers)) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_MAKERS);
            }
            makersPaycheck.updatePaycheckStatus(paycheckStatus);
        }
    }
}
