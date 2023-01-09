package co.dalicious.domain.application_form.dto.corporation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Schema(description = "기업 스팟 개설 신청 식사 정보 응답 DTO")
@Getter
@Setter
public class CorporationMealInfoResponseDto {
    private String diningType;
    private String priceAverage;
    private BigDecimal supportPrice;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;
}
