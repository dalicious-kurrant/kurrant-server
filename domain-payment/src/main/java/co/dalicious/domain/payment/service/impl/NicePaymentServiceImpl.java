package co.dalicious.domain.payment.service.impl;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.service.PaymentService;
import co.dalicious.domain.payment.util.NiceUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Primary
@RequiredArgsConstructor
public class NicePaymentServiceImpl implements PaymentService {
    private final NiceUtil niceUtil;
    @Override
    public CreditCardDto.Response getBillingKey(String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException {
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
}
