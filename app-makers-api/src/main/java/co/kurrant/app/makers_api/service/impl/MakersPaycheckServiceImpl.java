package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckMemo;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.MakersPaycheckMapper;
import co.dalicious.domain.paycheck.repository.MakersPaycheckRepository;
import co.dalicious.domain.paycheck.repository.QMakersPaycheckRepository;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersPaycheckService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MakersPaycheckServiceImpl implements MakersPaycheckService {
    private final MakersPaycheckMapper makersPaycheckMapper;
    private final MakersPaycheckRepository makersPaycheckRepository;
    private final QMakersPaycheckRepository qMakersPaycheckRepository;
    private final PaycheckService paycheckService;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public PaycheckDto.MakersResponse getMakersPaychecks(SecurityUser securityUser, Map<String, Object> parameters) {
        String startYearMonth = !parameters.containsKey("startYearMonth") || parameters.get("startYearMonth") == null ? null : String.valueOf(parameters.get("startYearMonth"));
        String endYearMonth = !parameters.containsKey("endYearMonth") || parameters.get("endYearMonth") == null ? null : String.valueOf(parameters.get("endYearMonth"));
        Integer status = !parameters.containsKey("status") || parameters.get("status") == null ? null : Integer.parseInt(String.valueOf(parameters.get("status")));
        Boolean hasRequest = !parameters.containsKey("hasRequest") || parameters.get("hasRequest") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("hasRequest")));

        YearMonth start = startYearMonth == null ? null : YearMonth.parse(startYearMonth.substring(0, 4) + "-" + startYearMonth.substring(4));
        YearMonth end = endYearMonth == null ? null : YearMonth.parse(endYearMonth.substring(0, 4) + "-" + endYearMonth.substring(4));

        Makers makers = userUtil.getMakers(securityUser);
        List<MakersPaycheck> makersPaychecks = qMakersPaycheckRepository.getMakersPaychecksByFilter(makers, start, end, PaycheckStatus.ofCode(status), hasRequest);
        return makersPaycheckMapper.toMakersResponse(makersPaychecks);
    }

    @Override
    @Transactional
    public PaycheckDto.MakersDetail getPaycheckDetail(SecurityUser securityUser, BigInteger paycheckId) {
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        Makers makers = userUtil.getMakers(securityUser);

        if(!makersPaycheck.getMakers().equals(makers)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        TransactionInfoDefault transactionInfoDefault = paycheckService.getTransactionInfoDefault();
        transactionInfoDefault.updateYearMonth(DateUtils.YearMonthToString(makersPaycheck.getYearMonth()));
        return makersPaycheckMapper.toDetailDto(makersPaycheck, transactionInfoDefault);
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

    @Override
    @Transactional
    public void postMemo(SecurityUser securityUser, BigInteger paycheckId, PaycheckDto.MemoDto memoDto) {
        Makers makers = userUtil.getMakers(securityUser);
        MakersPaycheck makersPaycheck = makersPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        if(!makersPaycheck.getMakers().equals(makers)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        String writer = makers.getName() + " 사장님";
        makersPaycheck.updateMemo(new PaycheckMemo(writer, memoDto.getMemo()));
    }
}
