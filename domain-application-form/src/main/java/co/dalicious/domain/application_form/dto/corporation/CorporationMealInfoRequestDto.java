package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class CorporationMealInfoRequestDto {
    private Integer diningType;
    private Integer priceAverage;
    private BigDecimal supportPrice;
    private Integer expectedUserCount;
    private List<Integer> serviceDays;
    private String deliveryTime;
    private CorporationApplicationForm corporationApplicationForm;

    public void setCorporationApplicationForm(CorporationApplicationForm corporationApplicationForm) {
        this.corporationApplicationForm = corporationApplicationForm;
    }
}
