package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoRequestDto;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "application_form__apartment_meal_info")
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

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_form_apartment_id")
    @JsonBackReference(value = "application_form_apartment_fk")
    private ApartmentApplicationForm apartmentApplicationForm;

    @Builder
    public ApartmentMealInfo(ApartmentMealInfoRequestDto apartmentMealInfoRequestDto, ApartmentApplicationForm apartmentApplicationForm) {
        this.diningType = DiningType.ofCode(apartmentMealInfoRequestDto.getDiningType());
        this.expectedUserCount = apartmentMealInfoRequestDto.getExpectedUserCount();
        this.serviceDays = DaysUtil.serviceDaysToDbData(apartmentMealInfoRequestDto.getServiceDays());
        this.deliveryTime = apartmentMealInfoRequestDto.getDeliveryTime();
        this.apartmentApplicationForm = apartmentApplicationForm;
    }
}
