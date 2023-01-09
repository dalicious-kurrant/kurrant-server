package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "기업 식사 정보 저장 DTO")
@Getter
@Setter
public class CorporationMealInfoRequestDto {
    private Integer diningType;
    private Integer priceAverage;
    private BigDecimal supportPrice;
    private Integer expectedUserCount;
    private List<Integer> serviceDays;
    private String deliveryTime;
}
