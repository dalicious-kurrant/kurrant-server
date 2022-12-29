package co.dalicious.domain.application_form.dto;

import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplyMealInfoRequestDto {
    private Integer diningType;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;
    private ApartmentApplicationForm apartmentApplicationForm;

    @Builder
    public ApplyMealInfoRequestDto(Integer diningType, Integer expectedUserCount, String serviceDays, String deliveryTime, ApartmentApplicationForm apartmentApplicationForm) {
        this.diningType = diningType;
        this.expectedUserCount = expectedUserCount;
        this.serviceDays = serviceDays;
        this.deliveryTime = deliveryTime;
        this.apartmentApplicationForm = apartmentApplicationForm;
    }

    public void insertApplicationFormApartment(ApartmentApplicationForm apartmentApplicationForm) {
        this.apartmentApplicationForm = apartmentApplicationForm;
    }
}
