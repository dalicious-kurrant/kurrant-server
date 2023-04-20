package co.dalicious.domain.food.entity;

import co.dalicious.domain.client.converter.DayAndTimeConverter;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.dto.MakersCapacityDto;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;

@DynamicUpdate
@DynamicInsert
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__makers_capacity")
public class MakersCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "makers_fk")
    private Makers makers;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사타입")
    private DiningType diningType;

    @Comment("식사 일정별 가능 수량")
    private Integer capacity;

    @Column(name = "last_order_time")
    @Convert(converter = DayAndTimeConverter.class)
    @Comment("음식별 주문 마감 시간")
    private DayAndTime lastOrderTime;
    @Builder
    public MakersCapacity(Makers makers, DiningType diningType, Integer capacity, DayAndTime lastOrderTime) {
        this.makers = makers;
        this.diningType = diningType;
        this.capacity = capacity;
        this.lastOrderTime = lastOrderTime;
    }

    public void updateMakersCapacity(Makers makers, MakersCapacityDto makersCapacity) {
        this.makers = makers;
        this.diningType = DiningType.ofCode(makersCapacity.getDiningType());
        this.capacity = makersCapacity.getCapacity();
        this.lastOrderTime = DayAndTime.stringToDayAndTime(makersCapacity.getLastOrderTime());
    }
}
