package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.MakersPaycheckRepository;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.PaycheckService;
import co.kurrant.app.makers_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaycheckServiceImpl implements PaycheckService {
    private MakersPaycheckMapper makersPaycheckMapper;
    private MakersPaycheckRepository makersPaycheckRepository;
    private UserUtil userUtil;

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
            makersPaycheck.updatePaycheckStatus(paycheckStatus);
        }
    }
}
