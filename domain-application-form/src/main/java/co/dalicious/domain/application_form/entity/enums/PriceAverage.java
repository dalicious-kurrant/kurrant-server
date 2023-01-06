package co.dalicious.domain.application_form.entity.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum PriceAverage {
    PRICE_AVERAGE_1(BigDecimal.valueOf(8000.0), BigDecimal.valueOf(12000.0), "8,000원 - 12,000원", 0),
    PRICE_AVERAGE_2(BigDecimal.valueOf(12000.0), BigDecimal.valueOf(15000.0), "12,000원 - 15,000원", 1),
    PRICE_AVERAGE_3(BigDecimal.valueOf(15000.0), BigDecimal.valueOf(20000.0), "15,000원 - 20,000원", 2);

    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final String priceAverage;
    private final Integer code;

    PriceAverage(BigDecimal minPrice, BigDecimal maxPrice, String priceAverage, Integer code) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.priceAverage = priceAverage;
        this.code = code;
    }

    public static PriceAverage ofCode(Integer dbData) {
        return Arrays.stream(PriceAverage.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식단 가격 범위입니다."));
    }
}
