package co.dalicious.domain.payment.service;

import co.dalicious.domain.payment.dto.CreditCardDto;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface PaymentService {
    // 빌링키를 가져온다
    CreditCardDto.Response getBillingKey(String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException;
}
