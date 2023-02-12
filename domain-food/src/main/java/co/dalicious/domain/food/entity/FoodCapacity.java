package co.dalicious.domain.food.entity;

import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.converter.DiningTypeConverter;
import co.dalicious.system.util.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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
}
