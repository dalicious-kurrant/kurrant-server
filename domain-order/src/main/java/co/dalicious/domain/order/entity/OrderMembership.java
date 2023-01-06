package co.dalicious.domain.order.entity;

import co.dalicious.domain.user.converter.MembershipSubscriptionTypeConverter;
import co.dalicious.domain.user.entity.Membership;
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
import java.math.BigInteger;
import java.sql.Timestamp;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor
@Table(name = "order__order_membership")
public class OrderMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("멤버십 결제 PK")
    private BigInteger id;


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

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @Builder
    public OrderMembership(Order order, Membership membership) {
        this.order = order;
        this.membership = membership;
    }
}
