package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoRequestDto;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "application_form__meal_info")
public class ApartmentMealInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("식사 상세 정보 DTO")
    private Long id;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 타입")
    private DiningType diningType;

    @Comment("예상 이용 인원수")
    private Integer expectedUserCount;

    @Comment("이용 날짜")
    private String serviceDays;

    @Comment("배달 시간")
    private String deliveryTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_form_apartment_id")
    @JsonBackReference(value = "application_form_apartment_fk")
    private ApartmentApplicationForm apartmentApplicationForm;

    @Builder
    public ApartmentMealInfo(ApartmentMealInfoRequestDto apartmentMealInfoRequestDto, ApartmentApplicationForm apartmentApplicationForm) {
        this.diningType = DiningType.ofCode(apartmentMealInfoRequestDto.getDiningType());
        this.expectedUserCount = apartmentMealInfoRequestDto.getExpectedUserCount();
        this.serviceDays = apartmentMealInfoRequestDto.getServiceDays();
        this.deliveryTime = apartmentMealInfoRequestDto.getDeliveryTime();
        this.apartmentApplicationForm = apartmentApplicationForm;
    }
}
