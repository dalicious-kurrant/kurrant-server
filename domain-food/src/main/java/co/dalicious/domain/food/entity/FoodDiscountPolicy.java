package co.dalicious.domain.food.entity;

import co.dalicious.system.util.converter.DiscountTypeConverter;
import co.dalicious.system.util.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"food_id", "discount_type"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FoodDiscountPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("할인 타입")
    @Convert(converter = DiscountTypeConverter.class)
    @Column(name = "discount_type")
    private DiscountType discountType;

    @Comment("할인율")
    private Integer discountRate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id")
    @JsonManagedReference(value = "food_fk")
    private Food food;
}
