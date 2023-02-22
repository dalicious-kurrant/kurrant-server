package co.dalicious.system.util;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class PriceUtils {
    public static BigDecimal roundToOneDigit(BigDecimal bigDecimal) {
        int integer = bigDecimal.intValue();
        integer = (integer + 5) / 10 * 10;
        return BigDecimal.valueOf(integer);
    }
}
