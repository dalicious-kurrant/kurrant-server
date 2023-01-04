package co.dalicious.domain.application_form.dto.apartment;

import co.dalicious.domain.application_form.entity.ApartmentApplicationMealInfo;
import co.dalicious.system.util.DateUtils;
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
    public ApartmentMealInfoResponseDto(ApartmentApplicationMealInfo apartmentApplicationMealInfo) {
        this.diningType = apartmentApplicationMealInfo.getDiningType().getDiningType();
        this.expectedUserCount = apartmentApplicationMealInfo.getExpectedUserCount();
        this.serviceDays = DaysUtil.serviceDaysToString(apartmentApplicationMealInfo.getServiceDays());
        this.deliveryTime = DateUtils.timeToString(apartmentApplicationMealInfo.getDeliveryTime());
    }
}
