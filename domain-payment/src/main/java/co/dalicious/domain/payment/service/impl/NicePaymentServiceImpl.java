package co.dalicious.domain.payment.service.impl;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.dto.PaymentCancelResponseDto;
import co.dalicious.domain.payment.dto.PaymentResponseDto;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.payment.service.PaymentService;
import co.dalicious.domain.payment.util.NiceUtil;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Primary
@RequiredArgsConstructor
public class NicePaymentServiceImpl implements PaymentService {
    private final NiceUtil niceUtil;
    @Override
    public CreditCardDto.Response getBillingKey(String corporationCode, String cardType, String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException {
        String token = niceUtil.getToken();

        String customerKey = niceUtil.createCustomerKey();

        JSONObject jsonObject = niceUtil.cardRegisterRequest(cardNumber, expirationYear, expirationMonth,
                cardPassword, identityNumber, customerKey, token);

        System.out.println(jsonObject + " jsonObject");

        CreditCardDto.Response response = new CreditCardDto.Response();
        String returnedCardNumber = (String) jsonObject.get("card_number");

        response.setBillingKey((String) jsonObject.get("customer_uid"));
        response.setCustomerKey((String) jsonObject.get("customer_id"));
        response.setCardNumber(returnedCardNumber);
        response.setCardCompany((String) jsonObject.get("card_name"));

        return response;
    }

    @Override
    public void deleteBillingKey(String bid) {

    }

    @Override
    public PaymentResponseDto pay(User user, String billingKey, Integer totalPrice, String orderCode, String orderName) throws IOException, ParseException {
        if (billingKey == null) {
            throw new ApiException(ExceptionEnum.CARD_NOT_FOUND);
        }

        String token = niceUtil.getToken();
        JSONObject jsonObject = niceUtil.niceBilling(billingKey, totalPrice, orderCode, token, orderName);

        Long code = (Long) jsonObject.get("code");
        JSONObject response = (JSONObject) jsonObject.get("response");
        String status = response.get("status").toString();

        if(code != 0 || status.equals("failed")) {
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }

        return PaymentResponseDto.builder()
                .receipt(response.get("receipt_url").toString())
                .transactionCode((String) response.get("imp_uid"))
                .paymentCompany(PaymentCompany.ofValue(response.get("card_name").toString()))
                .build();
    }

    @Override
    public PaymentResponseDto payQuota(User user, String billingKey, Integer totalPrice, String orderCode, String orderName, Integer quotaMonth) throws IOException, ParseException {
        String token = niceUtil.getToken();
        JSONObject jsonObject = niceUtil.niceBillingCardQuota(billingKey, totalPrice,orderCode, token, orderName, quotaMonth);
        System.out.println(jsonObject);
        return null;
    }

    @Override
    public PaymentCancelResponseDto cancelAll(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        return getPaymentCancelResponseDto(transactionKey, cancelAmount, cancelReason);
    }

    @Override
    public PaymentCancelResponseDto cancelPartial(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        return getPaymentCancelResponseDto(transactionKey, cancelAmount, cancelReason);
    }

    private PaymentCancelResponseDto getPaymentCancelResponseDto(String transactionKey, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        String token = niceUtil.getToken();

        JSONObject response = niceUtil.cardCancelOne(transactionKey, cancelReason, cancelAmount, token);

        JSONArray checkout = (JSONArray) response.get("cancel_receipt_urls");
        String receiptUrl = (String) checkout.get(0);
        String code = response.get("merchant_uid").toString();
        long cancelPrice = (long) response.get("cancel_amount");
        return PaymentCancelResponseDto.builder()
                .orderCode(code)
                .receiptUrl(receiptUrl)
                .cancelAmount(BigDecimal.valueOf(cancelPrice))
                .build();
    }
}
