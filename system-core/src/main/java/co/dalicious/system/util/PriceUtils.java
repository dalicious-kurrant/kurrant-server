package co.dalicious.system.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {
    public static BigDecimal roundToOneDigit(BigDecimal bigDecimal) {
        BigDecimal roundedValue = bigDecimal.setScale(1, RoundingMode.HALF_UP);
        return roundedValue.stripTrailingZeros();
    }
}
