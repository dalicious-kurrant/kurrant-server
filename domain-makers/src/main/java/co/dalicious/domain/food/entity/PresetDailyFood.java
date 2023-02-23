package co.dalicious.domain.food.entity;

import co.dalicious.domain.food.converter.ScheduleStatusConverter;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__preset_daily_food")
public class PresetDailyFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("Id")
    private BigInteger id;

    @Column(name = "capacity")
    @Comment("식사 일정과 날짜별 가능한 주문 수량")
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("음식")
    private Food food;

    @Convert(converter = ScheduleStatusConverter.class)
    @Column(name = "e_status")
    @Comment("식단 상태(0. 승인대기 1. 승인, 2. 거절)")
    private ScheduleStatus scheduleStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preset_group_daily_food_id")
    @JsonManagedReference(value = "preset_group_daily_food_fk")
    @Comment("고객사별 예비 식단 ID")
    private PresetGroupDailyFood presetGroupDailyFood;

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) ON UPDATE NOW(6) COMMENT '수정일'")
    private Timestamp updatedDateTime;

    @Builder
    public PresetDailyFood(Integer capacity,Food food,ScheduleStatus scheduleStatus,PresetGroupDailyFood presetGroupDailyFood) {
        this.capacity = capacity;
        this.food = food;
        this.scheduleStatus = scheduleStatus;
        this.presetGroupDailyFood = presetGroupDailyFood;
    }
    public void updateStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
