package co.dalicious.system.util;

import java.math.BigDecimal;

public class PriceCalculate {
    public static Integer membershipDiscount(Integer price, Integer membershipDiscountRate, Integer discountRate, Integer periodDiscountRate){
        Integer membershipDiscountPrice = 0;

        membershipDiscountPrice = price * (100-membershipDiscountPrice) / 100;
        membershipDiscountPrice = membershipDiscountRate * ( 100 - discountRate) / 100;
        membershipDiscountPrice = membershipDiscountRate * (100 - periodDiscountRate) / 100;

        return membershipDiscountPrice;
    }

    public static Integer discount(Integer price, Integer discountRate, Integer periodDiscountRate){
        Integer discountPrice = 0;
        discountPrice = price * (100 - discountRate) / 100;
        discountPrice = discountPrice * (100 - periodDiscountRate) / 100;
        return discountPrice;
    }

    public static Integer membershipOneDiscount(Integer price, Integer discountRate){
        price = price * (100 - discountRate) / 100;
        return price;
    }

}
