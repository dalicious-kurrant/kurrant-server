package co.dalicious.domain.application_form.dto;

import co.dalicious.domain.application_form.entity.ApplyMealInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplyMealInfoResponseDto {
    private String diningType;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;

    @Builder
    public ApplyMealInfoResponseDto(ApplyMealInfo applyMealInfo) {
        this.diningType = applyMealInfo.getDiningType().getDiningType();
        this.expectedUserCount = applyMealInfo.getExpectedUserCount();
        this.serviceDays = applyMealInfo.getServiceDays();
        this.deliveryTime = applyMealInfo.getDeliveryTime();
    }
}
