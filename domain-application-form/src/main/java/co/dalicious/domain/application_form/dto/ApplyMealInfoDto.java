package co.dalicious.domain.application_form.dto;

import co.dalicious.domain.application_form.entity.ApplicationFormApartment;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@NoArgsConstructor
public class ApplyMealInfoDto {
    private Integer diningType;
    private Integer expectedUserCount;
    private String serviceDays;
    private String deliveryTime;
    private ApplicationFormApartment applicationFormApartment;

    @Builder
    public ApplyMealInfoDto(Integer diningType, Integer expectedUserCount, String serviceDays, String deliveryTime, ApplicationFormApartment applicationFormApartment) {
        this.diningType = diningType;
        this.expectedUserCount = expectedUserCount;
        this.serviceDays = serviceDays;
        this.deliveryTime = deliveryTime;
        this.applicationFormApartment = applicationFormApartment;
    }

    public void insertApplicationFormApartment(ApplicationFormApartment applicationFormApartment) {
        this.applicationFormApartment = applicationFormApartment;
    }
}
