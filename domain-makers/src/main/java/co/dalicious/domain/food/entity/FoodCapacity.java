package co.dalicious.domain.food.entity;

import co.dalicious.domain.client.converter.DayAndTimeConverter;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__food_capacity")
public class FoodCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "food_fk")
    private Food food;

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
    public FoodCapacity(Food food, DiningType diningType, Integer capacity) {
        this.food = food;
        this.diningType = diningType;
        this.capacity = capacity;
    }

    public void updateCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
