package co.dalicious.domain.food.entity;

import co.dalicious.domain.food.converter.ConfirmStatusConverter;
import co.dalicious.domain.food.converter.ScheduleStatusConverter;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__preset_makers_daily_food")
public class PresetMakersDailyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Column(name = "service_date")
    @Comment("메이커스 서비스일")
    private LocalDate serviceDate;

    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "dining_type")
    @Comment("식단 타입")
    private DiningType diningType;

    @Column(name = "capacity")
    @Comment("식사 일정과 날짜별 가능한 주문 수량")
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("메이커스")
    private Makers makers;

    @Convert(converter = ScheduleStatusConverter.class)
    @Column(name = "e_schedule_status")
    @Comment("식단 상태 (0. 승인대기 1. 승인, 2. 거절)")
    private ScheduleStatus scheduleStatus;

    @Column(name = "deadline_datetime")
    @Comment("식단 승인 마감시간")
    private LocalDateTime deadline;

    @Convert(converter = ConfirmStatusConverter.class)
    @Column(name = "e_comfirm_status")
    @Comment("임시저장 상태 (0. 임시저장, 1. 요청, 2. 완료)")
    private ConfirmStatus confirmStatus;

    @OneToMany(mappedBy = "presetMakersDailyFood", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "preset_makers_daily_food_fk")
    @Comment("고객사 예비 식단")
    private List<PresetGroupDailyFood> presetGroupDailyFoods;

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) ON UPDATE NOW(6) COMMENT '수정일'")
    private Timestamp updatedDateTime;

    @Builder
    public PresetMakersDailyFood(LocalDate serviceDate, DiningType diningType, Integer capacity, Makers makers, ScheduleStatus scheduleStatus, LocalDateTime deadline, ConfirmStatus confirmStatus) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.capacity = capacity;
        this.makers = makers;
        this.scheduleStatus = scheduleStatus;
        this.deadline = deadline;
        this.confirmStatus = confirmStatus;
    }

    public void updateScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public void updatePresetMakersDailyFood(ScheduleStatus scheduleStatus, LocalDateTime deadline, ConfirmStatus confirmStatus) {
        this.scheduleStatus = scheduleStatus;
        this.deadline = deadline;
        this.confirmStatus = confirmStatus;
    }

    public void updateConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
}
