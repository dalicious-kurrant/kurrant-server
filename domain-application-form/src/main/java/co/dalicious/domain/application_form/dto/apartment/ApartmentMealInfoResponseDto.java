package co.dalicious.domain.application_form.dto.apartment;

import co.dalicious.domain.application_form.entity.ApartmentMealInfo;
import co.dalicious.system.util.DaysUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApartmentMealInfoResponseDto {
    private String diningType;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;

    @Builder
    public ApartmentMealInfoResponseDto(ApartmentMealInfo apartmentMealInfo) {
        this.diningType = apartmentMealInfo.getDiningType().getDiningType();
        this.expectedUserCount = apartmentMealInfo.getExpectedUserCount();
        this.serviceDays = DaysUtil.serviceDaysToString(apartmentMealInfo.getServiceDays());
        this.deliveryTime = apartmentMealInfo.getDeliveryTime();
    }
}
