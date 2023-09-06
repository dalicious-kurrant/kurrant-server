package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFoodGroup;
import co.dalicious.domain.order.repository.QDailyFoodSupportPriceRepository;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.dto.TransactionInfoDefault;
import co.dalicious.domain.paycheck.entity.CorporationPaycheck;
import co.dalicious.domain.paycheck.entity.PaycheckMemo;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import co.dalicious.domain.paycheck.mapper.CorporationPaycheckMapper;
import co.dalicious.domain.paycheck.repository.CorporationPaycheckRepository;
import co.dalicious.domain.paycheck.repository.QCorporationPaycheckRepository;
import co.dalicious.domain.paycheck.service.PaycheckService;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientPaycheckService;
import co.kurrant.app.client_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientPaycheckServiceImpl implements ClientPaycheckService {
    private final UserUtil userUtil;
    private final CorporationPaycheckRepository corporationPaycheckRepository;
    private final QCorporationPaycheckRepository qCorporationPaycheckRepository;
    private final CorporationPaycheckMapper corporationPaycheckMapper;
    private final QDailyFoodSupportPriceRepository qDailyFoodSupportPriceRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final PaycheckService paycheckService;

    @Override
    @Transactional
    public List<PaycheckDto.CorporationResponse> getCorporationPaychecks(SecurityUser securityUser, Map<String, Object> parameters) {
        String startYearMonth = !parameters.containsKey("startYearMonth") || parameters.get("startYearMonth") == null ? null : String.valueOf(parameters.get("startYearMonth"));
        String endYearMonth = !parameters.containsKey("endYearMonth") || parameters.get("endYearMonth") == null ? null : String.valueOf(parameters.get("endYearMonth"));
        Integer status = !parameters.containsKey("status") || parameters.get("status") == null ? null : Integer.parseInt(String.valueOf(parameters.get("status")));
        Boolean hasRequest = !parameters.containsKey("hasRequest") || parameters.get("hasRequest") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("hasRequest")));

        YearMonth start = startYearMonth == null ? null : YearMonth.parse(startYearMonth.substring(0, 4) + "-" + startYearMonth.substring(4));
        YearMonth end = endYearMonth == null ? null : YearMonth.parse(endYearMonth.substring(0, 4) + "-" + endYearMonth.substring(4));

        Corporation corporation = userUtil.getCorporation(securityUser);
        List<CorporationPaycheck> corporationPaychecks = qCorporationPaycheckRepository.getCorporationPaychecksByFilter(corporation, start, end, PaycheckStatus.ofCode(status), hasRequest);
        return corporationPaycheckMapper.toDtos(corporationPaychecks);
    }

    @Override
    @Transactional
    public PaycheckDto.CorporationOrder getPaycheckOrders(SecurityUser securityUser, BigInteger paycheckId) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        Corporation corporation = userUtil.getCorporation(securityUser);

        if(!corporationPaycheck.getCorporation().equals(corporation)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        YearMonth yearMonth = corporationPaycheck.getYearMonth();
//        List<DailyFoodSupportPrice> dailyFoodSupportPrices = qDailyFoodSupportPriceRepository.findAllByGroupAndPeriod(corporation, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        List<OrderItemDailyFoodGroup> orderItemDailyFoodGroups = qOrderDailyFoodRepository.findAllOrderItemDailyFoodGroupByGroup(corporation, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        return corporationPaycheckMapper.toCorporationOrder(corporation, orderItemDailyFoodGroups);
    }

    @Override
    @Transactional
    public PaycheckDto.Invoice getPaycheckInvoice(SecurityUser securityUser, BigInteger paycheckId) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        Corporation corporation = userUtil.getCorporation(securityUser);

        if(!corporationPaycheck.getCorporation().equals(corporation)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        PaycheckDto.Invoice invoice = corporationPaycheckMapper.toInvoice(corporationPaycheck, 0);
        TransactionInfoDefault transactionInfoDefault = paycheckService.getTransactionInfoDefault();
        transactionInfoDefault.setYearMonth(DateUtils.YearMonthToString(corporationPaycheck.getYearMonth()));
        invoice.setTransactionInfoDefault(transactionInfoDefault);
        return invoice;
    }

    @Override
    @Transactional
    public void updateCorporationPaycheckStatus(SecurityUser securityUser, Integer code, List<BigInteger> ids) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        PaycheckStatus paycheckStatus = PaycheckStatus.ofCode(code);
        List<CorporationPaycheck> corporationPaychecks = corporationPaycheckRepository.findAllByCorporationAndIdIn(corporation, ids);
        for (CorporationPaycheck corporationPaycheck : corporationPaychecks) {
            if (!corporationPaycheck.getCorporation().equals(corporation)) {
                throw new ApiException(ExceptionEnum.NOT_MATCHED_GROUP);
            }
            corporationPaycheck.updatePaycheckStatus(paycheckStatus);
        }
    }

    @Override
    @Transactional
    public void completeCorporationPaycheckStatus(SecurityUser securityUser, BigInteger id) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(id)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        Corporation corporation = userUtil.getCorporation(securityUser);
        if (!corporationPaycheck.getCorporation().equals(corporation)) {
            throw new ApiException(ExceptionEnum.NOT_MATCHED_GROUP);
        }
        corporationPaycheck.updatePaycheckStatus(PaycheckStatus.TRANSACTION_CONFIRM);
    }

    @Override
    @Transactional
    public void postMemo(SecurityUser securityUser, BigInteger paycheckId, PaycheckDto.MemoDto memoDto) {
        CorporationPaycheck corporationPaycheck = corporationPaycheckRepository.findById(paycheckId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        Corporation corporation = userUtil.getCorporation(securityUser);
        String writer = corporation.getName() + " 관리자";
        PaycheckMemo paycheckMemo = new PaycheckMemo(writer, memoDto.getMemo());
        corporationPaycheck.updateMemo(paycheckMemo);
    }
}
