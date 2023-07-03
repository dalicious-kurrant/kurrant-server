package co.dalicious.domain.food.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__preset_group_daily_food")
public class PresetGroupDailyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("고객사")
    private Group group;

    @Column(name = "capacity")
    @Comment("고객사 별 유저수")
    private Integer capacity;

    @ElementCollection
    @Comment("배송 시간")
    @CollectionTable(name = "makers__preset_delivery_schedule")
    private List<DeliverySchedule> deliveryScheduleList;

    @OneToMany(mappedBy = "presetGroupDailyFood", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "preset_group_daily_food_fk")
    @Comment("음식별 예비 식단")
    private List<PresetDailyFood> presetDailyFoods;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preset_makers_daily_food_id")
    @JsonManagedReference(value = "preset_makers_daily_food_fk")
    @Comment("고객사별 예비 식단 ID")
    private PresetMakersDailyFood presetMakersDailyFood;

    @Builder
    public PresetGroupDailyFood(Group group, Integer capacity, List<DeliverySchedule> deliveryScheduleList, PresetMakersDailyFood presetMakersDailyFood) {
        this.group = group;
        this.capacity = capacity;
        this.deliveryScheduleList = deliveryScheduleList;
        this.presetMakersDailyFood = presetMakersDailyFood;
    }
}
