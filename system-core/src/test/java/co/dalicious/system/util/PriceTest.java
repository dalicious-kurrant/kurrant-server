package co.dalicious.system.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class PriceTest {
    @Test
    @DisplayName("가격 체크")
    void Test() {
        BigDecimal price = new BigDecimal(4000L);

        BigDecimal membershipDiscountPrice = price.multiply(BigDecimal.valueOf(0.15));
        System.out.println("membershipDiscountPrice = " + membershipDiscountPrice);
        membershipDiscountPrice = NumberUtils.roundToOneDigit(membershipDiscountPrice);
        BigDecimal newMembershipDiscountPrice = membershipDiscountPrice;
        System.out.println("membershipDiscountPrice = " + membershipDiscountPrice);
        System.out.println("newMembershipDiscountPrice = " + newMembershipDiscountPrice);
        price = price.subtract(membershipDiscountPrice);

        BigDecimal makersDiscountPrice = price.multiply(BigDecimal.valueOf(0.2));
        System.out.println("makersDiscountPrice = " + makersDiscountPrice);
        makersDiscountPrice = NumberUtils.roundToOneDigit(makersDiscountPrice);
        System.out.println("makersDiscountPrice = " + makersDiscountPrice);
        price = price.subtract(makersDiscountPrice);

        BigDecimal periodDiscountPrice = price.multiply(BigDecimal.valueOf(0.07));
        System.out.println("periodDiscountPrice = " + periodDiscountPrice);
        periodDiscountPrice = NumberUtils.roundToOneDigit(periodDiscountPrice);
        System.out.println("periodDiscountPrice = " + periodDiscountPrice);
        price = price.subtract(periodDiscountPrice);

        System.out.println("price = " + price);

        assertTrue(price.compareTo(BigDecimal.valueOf(2530)) == 0);
        assertTrue(membershipDiscountPrice.compareTo(BigDecimal.valueOf(600.00)) == 0);
        assertTrue(periodDiscountPrice.compareTo(BigDecimal.valueOf(190.00)) == 0);
    }
}
