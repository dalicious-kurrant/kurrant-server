package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoRequestDto;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "application_form__apartment_meal_info")
public class ApartmentApplicationMealInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("식사 상세 정보 DTO")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 타입")
    private DiningType diningType;

    @Comment("예상 이용 인원수")
    private Integer expectedUserCount;

    @Comment("이용 날짜")
    private String serviceDays;

    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "application_form_apartment_id")
    @JsonBackReference(value = "application_form_apartment_fk")
    private ApartmentApplicationForm apartmentApplicationForm;

    @Builder
    public ApartmentApplicationMealInfo(ApartmentMealInfoRequestDto apartmentMealInfoRequestDto) {
        this.diningType = DiningType.ofCode(apartmentMealInfoRequestDto.getDiningType());
        this.expectedUserCount = apartmentMealInfoRequestDto.getExpectedUserCount();
        this.serviceDays = DaysUtil.serviceDaysToDbData(apartmentMealInfoRequestDto.getServiceDays());
        this.deliveryTime = DateUtils.stringToTime(apartmentMealInfoRequestDto.getDeliveryTime(), ":");
    }

    public void setApartmentApplicationForm(ApartmentApplicationForm apartmentApplicationForm) {
        this.apartmentApplicationForm = apartmentApplicationForm;
    }
}
