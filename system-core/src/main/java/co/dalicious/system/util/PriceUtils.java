package co.dalicious.system.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {
    public static BigDecimal roundToOneDigit(BigDecimal bigDecimal) {
       return bigDecimal.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP)
               .multiply(BigDecimal.TEN);
    }
}
