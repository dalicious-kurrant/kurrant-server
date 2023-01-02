package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.converter.PriceAverageConverter;
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
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "application_form__corporation_meal_info")
public class CorporationMealInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    @Comment("식사 타입")
    private DiningType diningType;

    @NotNull
    @Convert(converter = PriceAverageConverter.class)
    @Column(name = "prica_average", nullable = false)
    @Comment("가격 범위")
    private PriceAverage priceAverage;

    @NotNull
    @Column(name = "daily_support_price", nullable = false, precision = 15)
    @Comment("일일 회사 지원금")
    private BigDecimal supportPrice;

    @NotNull
    @Column(name = "expected_people", nullable = false)
    @Comment("서비스 이용 예상 인원수")
    private Integer expectedUserCount;

    @NotNull
    @Column(name = "service_date", nullable = false)
    @Comment("서비스 이용 요일")
    private String serviceDays;

    @NotNull
    @Column(name = "delivery_time", nullable = false)
    @Comment("배송 시간")
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_form__corporation_id")
    @JsonBackReference(value = "application_form__corporation_fk")
    private CorporationApplicationForm corporationApplicationForm;

    @Builder
    public CorporationMealInfo(DiningType diningType, PriceAverage priceAverage, BigDecimal supportPrice, Integer expectedUserCount, String serviceDays, String deliveryTime) {
        this.diningType = diningType;
        this.priceAverage = priceAverage;
        this.supportPrice = supportPrice;
        this.expectedUserCount = expectedUserCount;
        this.serviceDays = serviceDays;
        this.deliveryTime = deliveryTime;
    }

    public void setApplicationFormCorporation(CorporationApplicationForm corporationApplicationForm) {
        this.corporationApplicationForm = corporationApplicationForm;
    }

}