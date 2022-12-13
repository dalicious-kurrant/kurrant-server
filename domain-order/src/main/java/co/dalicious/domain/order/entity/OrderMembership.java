package co.dalicious.domain.order.entity;

import co.dalicious.domain.user.converter.MembershipSubscriptionTypeConverter;
import co.dalicious.domain.user.entity.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor
@Table(name = "order__order_membership")
public class OrderMembership{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("멤버십 결제 PK")
    private Long id;

    @NotNull
    @Column(name = "e_subscription_type")
    @Convert(converter = MembershipSubscriptionTypeConverter.class)
    @Comment("멤버십 구독 타입(월간/연간)")
    private MembershipSubscriptionType membershipSubscriptionType;

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

    @Column(name = "discount_rate")
    @Comment("할인율")
    private int discount_rate;

    @Builder
    public OrderMembership(MembershipSubscriptionType membershipSubscriptionType) {
        this.membershipSubscriptionType = membershipSubscriptionType;
    }
}
