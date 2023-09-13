package co.dalicious.domain.payment.service.impl;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.dto.PaymentCancelResponseDto;
import co.dalicious.domain.payment.dto.PaymentResponseDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.payment.service.PaymentService;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PointStatus;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class TossPayServiceImpl implements PaymentService {
    private final TossUtil tossUtil;
    @Override
    public CreditCardDto.Response getBillingKey(String corporationCode, String cardType, String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException {
        return null;
    }

    @Override
    public void deleteBillingKey(String billingKey) throws IOException, ParseException {

    }

    @Override
    public PaymentResponseDto pay(User user, CreditCardInfo creditCardInfo, Integer totalPrice, String orderCode, String orderName) throws IOException, ParseException {
        JSONObject jsonObject = tossUtil.paymentConfirm(creditCardInfo.getTossBillingKey(), totalPrice, orderCode);
        System.out.println(jsonObject + "결제 Response값");

        String status = (String) jsonObject.get("status");
        System.out.println(status);

        // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
        if (!status.equals("DONE")) {
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        }
        JSONObject receipt = (JSONObject) jsonObject.get("receipt");
        String receiptUrl = receipt.get("url").toString();

        String key = (String) jsonObject.get("paymentKey");
        JSONObject card = (JSONObject) jsonObject.get("card");
        String paymentCompanyCode;
        if (card == null) {
            JSONObject easyPay = (JSONObject) jsonObject.get("easyPay");
            if (easyPay == null) {
                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            }
            paymentCompanyCode = (String) easyPay.get("provider");
        } else {
            paymentCompanyCode = (String) card.get("issuerCode");
        }
        System.out.println("jsonObject = " + jsonObject);
        PaymentCompany paymentCompany = PaymentCompany.ofCode(paymentCompanyCode);
        return PaymentResponseDto.builder()
                .paymentCompany(paymentCompany)
                .transactionCode(key)
                .receipt(receiptUrl)
                .build();
    }

    @Override
    public PaymentResponseDto payQuota(User user, String billingKey, Integer totalPrice, String orderCode, String orderName, Integer quotaMonth) throws IOException, ParseException {
        return null;
    }

    @Override
    public PaymentCancelResponseDto cancelAll(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        return null;
    }

    @Override
    public PaymentCancelResponseDto cancelPartial(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException {
        JSONObject response = tossUtil.billingCardCancelOne(transactionKey, cancelReason, cancelAmount);
        System.out.println(response);

        String code = response.get("orderId").toString();

        JSONObject checkout = (JSONObject) response.get("checkout");
        String checkOutUrl = checkout.get("url").toString();
        JSONArray cancels = (JSONArray) response.get("cancels");
        Integer refundablePrice = null;

        if (cancels.size() != 0 && cancels.size() != 1) {
            for (Object cancel : cancels) {
                JSONObject cancel1 = (JSONObject) cancel;
                refundablePrice = Integer.valueOf(cancel1.get("refundableAmount").toString());
                System.out.println(refundablePrice + " = refundablePrice");
            }
        }
        JSONObject cancel = (JSONObject) cancels.get(0);
        refundablePrice = Integer.valueOf(cancel.get("refundableAmount").toString());

        JSONObject card = (JSONObject) response.get("card");
        String paymentCardNumber = card.get("number").toString();
        return PaymentCancelResponseDto.builder()
                .cancelAmount(BigDecimal.valueOf(cancelAmount))
                .orderCode(code)
                .receiptUrl(checkOutUrl)
                .build();
    }
}
