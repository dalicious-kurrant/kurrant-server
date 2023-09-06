package co.dalicious.domain.payment.service.impl;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.dto.PaymentCancelResponseDto;
import co.dalicious.domain.payment.dto.PaymentResponseDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.payment.service.PaymentService;
import co.dalicious.domain.payment.util.MingleUtil;
import co.dalicious.domain.user.entity.User;
import exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MinglePlayServiceImpl implements PaymentService {
    private final MingleUtil mingleUtil;

    @Override
    public CreditCardDto.Response getBillingKey(String corporationCode, String cardType, String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException {
        JSONObject jsonObject = mingleUtil.generateBillingKey(corporationCode, cardType, cardNumber, expirationYear, expirationMonth, identityNumber, cardPassword);
        String code = (String) jsonObject.get("resultCd");

        if(code.equals("F011")) {
            String message = (String) jsonObject.get("resultMsg");
            throw new CustomException(HttpStatus.BAD_REQUEST, message, "CE4000021");
        }

        CreditCardDto.Response creditCardDto = new CreditCardDto.Response();
        creditCardDto.setBillingKey((String) jsonObject.get("bid"));
        creditCardDto.setCardCompany((String) jsonObject.get("fnNm"));
        creditCardDto.setCardNumber(cardNumber.substring(0, 8) + "*".repeat(4) + cardNumber.substring(12, 16));
        return creditCardDto;
    }

    @Override
    public void deleteBillingKey(String bid) throws IOException, ParseException {
        JSONObject jsonObject = mingleUtil.deleteBillingKey(bid);

        String code = (String) jsonObject.get("resultCd");

        if(code.equals("F012")) {
            String message = (String) jsonObject.get("resultMsg");
            throw new CustomException(HttpStatus.BAD_REQUEST, message, "CE4000021");
        }
    }

    @Override
    public PaymentResponseDto pay(User user, CreditCardInfo creditCardInfo, Integer totalPrice, String orderCode, String orderName) throws IOException, ParseException {
        JSONObject jsonObject = mingleUtil.requestPayment(creditCardInfo.getMingleBillingKey(), orderCode, orderName, totalPrice, user.getName(), user.getPhone() == null ? "010" : user.getPhone(), user.getEmail());

        String code = (String) jsonObject.get("resultCd");
        if (!code.equals("3001")) {
            String message = (String) jsonObject.get("resultMsg");
            throw new CustomException(HttpStatus.BAD_REQUEST, message, "CE4000022");
        }

        return PaymentResponseDto.builder()
                .receipt(null)
                .transactionCode((String) jsonObject.get("tid"))
                .paymentCompany(PaymentCompany.ofMingleCode((String) jsonObject.get("cpCd")))
                .build();

    }

    @Override
    public PaymentResponseDto payQuota(User user, String billingKey, Integer totalPrice, String orderCode, String orderName, Integer quotaMonth) throws IOException, ParseException {
        return null;
    }

    @Override
    public PaymentCancelResponseDto cancelAll(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        JSONObject jsonObject = mingleUtil.cancelPayment(false, transactionKey, cancelAmount, orderCode, user.getId(), user.getName());
        return getPaymentCancelResponseDto(cancelReason, jsonObject);
    }

    @Override
    public PaymentCancelResponseDto cancelPartial(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        JSONObject jsonObject = mingleUtil.cancelPayment(true, transactionKey, cancelAmount, orderCode, user.getId(), user.getName());
        return getPaymentCancelResponseDto(cancelReason, jsonObject);
    }

    private PaymentCancelResponseDto getPaymentCancelResponseDto(String cancelReason, JSONObject jsonObject) {
        String code = (String) jsonObject.get("resultCd");
        if (!code.equals("2001")) {
            String message = (String) jsonObject.get("resultMsg");
            throw new CustomException(HttpStatus.BAD_REQUEST, message, "CE4000023");
        }
        return PaymentCancelResponseDto.builder()
                .orderCode((String) jsonObject.get("ordNo"))
                .cancelAmount(BigDecimal.valueOf(Long.parseLong((String) jsonObject.get("amt"))))
                .build();
    }
}
