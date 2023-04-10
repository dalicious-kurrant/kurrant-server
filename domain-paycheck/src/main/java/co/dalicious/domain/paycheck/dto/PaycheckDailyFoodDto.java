package co.dalicious.domain.paycheck.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class PaycheckDailyFoodDto {
    @Getter
    @Setter
    public static class Response {
        private String serviceDate;
        private String foodName;
        private BigDecimal supplyPrice;
        private Integer count;
        private BigDecimal totalPrice;
    }
}
