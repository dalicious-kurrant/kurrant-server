package co.dalicious.domain.order;

import java.math.BigInteger;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import co.dalicious.domain.order.entity.OrderType;
import co.dalicious.system.util.GenerateRandomNumber;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderUtil {
  // 주문 코드 생성
  public static String generateOrderCode(OrderType orderType, BigInteger userId) {
    String code = null;
    switch (orderType) {
      case DAILYFOOD:
        code = "S";
        break;
      case PRODUCT:
        code = "P";
        break;
      case MEMBERSHIP:
        code = "M";
        break;
      default:
        code = null;
    };
    LocalDate now = LocalDate.now();
    code += now.toString().replace("-", "");
    code += GenerateRandomNumber.idToString(userId.intValue());
    code += GenerateRandomNumber.create4DigitKey();
    return code;
  }
}
