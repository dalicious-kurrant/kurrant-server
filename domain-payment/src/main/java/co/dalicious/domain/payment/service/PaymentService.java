package co.dalicious.domain.payment.service;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.dto.PaymentCancelResponseDto;
import co.dalicious.domain.payment.dto.PaymentResponseDto;
import co.dalicious.domain.user.entity.User;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigDecimal;

public interface PaymentService {
    // 빌링키를 가져온다
    CreditCardDto.Response getBillingKey(String corporationCode, String cardType, String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException;

    // 빌링키를 삭제한다
    void deleteBillingKey(String billingKey) throws IOException, ParseException;

    // 빌링키를 통해 결제한다
    PaymentResponseDto pay(User user, String billingKey, Integer totalPrice, String orderCode, String orderName) throws IOException, ParseException;

    // 빌링키를 통해 할부 결제한다
    PaymentResponseDto payQuota(User user, String billingKey, Integer totalPrice, String orderCode, String orderName, Integer quotaMonth) throws IOException, ParseException;

    // PaymentKey를 통해 결제를 전체 취소한다.
    PaymentCancelResponseDto cancelAll(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException;

    // PaymentKey를 통해 결제를 부분 취소한다.
    PaymentCancelResponseDto cancelPartial(User user, String transactionKey, String orderCode, Integer cancelAmount, String cancelReason) throws IOException, ParseException;
}
