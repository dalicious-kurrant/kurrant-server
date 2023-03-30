package co.dalicious.system.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {
    public static BigDecimal roundToOneDigit(BigDecimal bigDecimal) {
        int integer = bigDecimal.intValue();
        integer = (integer + 5) / 10 * 10;
        return BigDecimal.valueOf(integer);
    }

    public static BigDecimal floorToOneDigit(BigDecimal bigDecimal) {
        int integer = bigDecimal.intValue();
        integer = integer / 10 * 10;
        return BigDecimal.valueOf(integer);
    }

    public static BigDecimal roundToTenDigit(BigDecimal bigDecimal) {
        int integer = bigDecimal.intValue();
        integer = (integer + 50) / 100 * 100;
        return BigDecimal.valueOf(integer);
    }

    public static BigDecimal floorToTenDigit(BigDecimal bigDecimal) {
        int integer = bigDecimal.intValue();
        integer = integer / 100 * 100;
        return BigDecimal.valueOf(integer);
    }

    public static BigDecimal getPercent(Integer totalCount, Integer count) {
        if (totalCount == null || totalCount == 0) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.valueOf(count)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        }
    }
}
