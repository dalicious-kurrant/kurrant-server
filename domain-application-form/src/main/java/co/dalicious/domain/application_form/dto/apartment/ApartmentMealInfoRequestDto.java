package co.dalicious.domain.application_form.dto.apartment;

import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApartmentMealInfoRequestDto {
    private Integer diningType;
    private Integer expectedUserCount;
    private List<Integer> serviceDays;
    private String deliveryTime;
    private ApartmentApplicationForm apartmentApplicationForm;

    @Builder
    public ApartmentMealInfoRequestDto(Integer diningType, Integer expectedUserCount, List<Integer> serviceDays, String deliveryTime, ApartmentApplicationForm apartmentApplicationForm) {
        this.diningType = diningType;
        this.expectedUserCount = expectedUserCount;
        this.serviceDays = serviceDays;
        this.deliveryTime = deliveryTime;
        this.apartmentApplicationForm = apartmentApplicationForm;
    }

    public void setApartmentApplicationForm(ApartmentApplicationForm apartmentApplicationForm) {
        this.apartmentApplicationForm = apartmentApplicationForm;
    }
}
