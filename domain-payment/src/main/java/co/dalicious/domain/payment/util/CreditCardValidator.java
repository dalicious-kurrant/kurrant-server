package co.dalicious.domain.payment.util;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.stereotype.Component;

@Component
public class CreditCardValidator {
    public static void isValidCreditCard(CreditCardInfo creditCardInfo, User user) {
        if(!creditCardInfo.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
