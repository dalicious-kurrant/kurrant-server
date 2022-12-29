package co.dalicious.domain.application_form.dto.corporation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CorporationMealInfoResponseDto {
    private String diningType;
    private String priceAverage;
    private BigDecimal supportPrice;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;

    @Builder
    public CorporationMealInfoResponseDto(String diningType, String priceAverage, BigDecimal supportPrice, Integer expectedUserCount, String serviceDays, String deliveryTime) {
        this.diningType = diningType;
        this.priceAverage = priceAverage;
        this.supportPrice = supportPrice;
        this.expectedUserCount = expectedUserCount;
        this.serviceDays = serviceDays;
        this.deliveryTime = deliveryTime;
    }
}
