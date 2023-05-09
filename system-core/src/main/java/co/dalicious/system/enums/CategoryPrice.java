package co.dalicious.system.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum CategoryPrice {
    DELIVERY_FEE_PER_ITEM(1, "1인당 배송비", BigDecimal.valueOf(500)),
    DELIVERY_FEE_BELOW_50(2, "50인 미만 배송비", BigDecimal.valueOf(25000)),
    GARBAGE_PER_ITEM(3, "1인당 쓰레기 수거비", BigDecimal.valueOf(300)),
    GARBAGE_PER_BELOW_50(4, "50인 미만 쓰레기 수거비", BigDecimal.valueOf(15000)),
    HOT_STORAGE(5, "온장고 대여료", BigDecimal.valueOf(20000)),
    MEAL_SETTING(6, "식사 세팅비", BigDecimal.valueOf(1000000));

    private Integer code;
    private String category;
    private BigDecimal price;

    CategoryPrice(Integer code, String category, BigDecimal price) {
        this.code = code;
        this.category = category;
        this.price = price;
    }
}
