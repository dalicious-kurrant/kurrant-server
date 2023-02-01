package co.kurrant.app.public_api.service;

import co.dalicious.domain.payment.dto.PaymentCancelRequestDto;
import co.kurrant.app.public_api.model.SecurityUser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface PaymentService {
    void paymentCancelOne(SecurityUser securityUser, PaymentCancelRequestDto paymentCancelRequestDto) throws IOException, ParseException;
}
