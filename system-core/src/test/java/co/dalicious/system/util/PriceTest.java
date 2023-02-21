package co.dalicious.system.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PriceTest {
    @Test
    @DisplayName("가격 체크")
    void Test() {
        BigDecimal price = new BigDecimal(4000L);

        BigDecimal membershipDiscountPrice = price.multiply(BigDecimal.valueOf(0.2));
        System.out.println("membershipDiscountPrice = " + membershipDiscountPrice);
        membershipDiscountPrice = PriceUtils.roundToOneDigit(membershipDiscountPrice);
        System.out.println("membershipDiscountPrice = " + membershipDiscountPrice);
        price = price.subtract(membershipDiscountPrice);

        BigDecimal makersDiscountPrice = price.multiply(BigDecimal.valueOf(0.15));
        System.out.println("makersDiscountPrice = " + makersDiscountPrice);
        makersDiscountPrice = PriceUtils.roundToOneDigit(makersDiscountPrice);
        System.out.println("makersDiscountPrice = " + makersDiscountPrice);
        price = price.subtract(makersDiscountPrice);

        BigDecimal periodDiscountPrice = price.multiply(BigDecimal.valueOf(0.07));
        System.out.println("periodDiscountPrice = " + periodDiscountPrice);
        periodDiscountPrice = PriceUtils.roundToOneDigit(periodDiscountPrice);
        System.out.println("periodDiscountPrice = " + periodDiscountPrice);
        price = price.subtract(periodDiscountPrice);

        System.out.println("price = " + price);
    }
}
