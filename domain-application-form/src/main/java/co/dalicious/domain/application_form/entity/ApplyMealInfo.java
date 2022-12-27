package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.dto.ApplyMealInfoDto;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "application_form__meal_info")
public class ApplyMealInfo {
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

    @ManyToOne
    @JsonBackReference(value = "application_form_apartment_fk")
    private ApplicationFormApartment applicationFormApartment;

    @Builder
    public ApplyMealInfo(ApplyMealInfoDto applyMealInfoDto, ApplicationFormApartment applicationFormApartment) {
        this.diningType = DiningType.ofCode(applyMealInfoDto.getDiningType());
        this.expectedUserCount = applyMealInfoDto.getExpectedUserCount();
        this.serviceDays = applyMealInfoDto.getServiceDays();
        this.deliveryTime = applyMealInfoDto.getDeliveryTime();
        this.applicationFormApartment = applicationFormApartment;
    }
}
