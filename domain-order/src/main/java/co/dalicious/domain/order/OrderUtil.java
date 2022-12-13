package co.dalicious.domain.order;

import co.dalicious.domain.order.entity.OrderType;
import co.dalicious.system.util.GenerateRandomNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrderUtil {
    // 주문 코드 생성
    public static String generateOrderCode(OrderType orderType, BigInteger userId) {
        String code = switch (orderType) {
            case DAILYFOOD -> "S";
            case PRODUCT -> "P";
            case MEMBERSHIP -> "M";
        };
        LocalDate now = LocalDate.now();
        code += now.toString().replace("-", "");
        code += GenerateRandomNumber.idToString(userId.intValue());
        code += GenerateRandomNumber.create4DigitKey();
        return code;
    }
}
