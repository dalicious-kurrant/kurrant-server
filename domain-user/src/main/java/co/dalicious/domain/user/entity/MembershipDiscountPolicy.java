package co.dalicious.domain.user.entity;

import co.dalicious.system.converter.DiscountTypeConverter;
import co.dalicious.system.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "membership__discount_policy", uniqueConstraints={@UniqueConstraint(columnNames={"membership_id", "discount_type"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MembershipDiscountPolicy {
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
    @JoinColumn(name = "membership_id")
    @JsonManagedReference(value = "membership_fk")
    private Membership membership;

    @Builder
    public MembershipDiscountPolicy(DiscountType discountType, Integer discountRate, Membership membership) {
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.membership = membership;
    }
}
