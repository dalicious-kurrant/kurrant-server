package co.dalicious.domain.payment.service.impl;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.service.PaymentService;
import co.dalicious.domain.payment.util.MingleUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MinglePlayServiceImpl implements PaymentService {
    private final MingleUtil mingleUtil;
    @Override
    public CreditCardDto.Response getBillingKey(String cardNumber, String expirationYear, String expirationMonth, String cardPassword, String identityNumber) throws IOException, ParseException {
        return null;
    }
}
