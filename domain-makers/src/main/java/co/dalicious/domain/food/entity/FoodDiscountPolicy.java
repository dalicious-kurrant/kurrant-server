package co.dalicious.domain.food.entity;

import co.dalicious.system.converter.DiscountTypeConverter;
import co.dalicious.system.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Builder
    public FoodDiscountPolicy(Food food, DiscountType discountType, Integer discountRate) {
        this.food = food;
        this.discountType = discountType;
        this.discountRate = discountRate;
    }

    public void updateFoodDiscountPolicy(Integer discountRate) {
        this.discountRate = discountRate;
    }

}
