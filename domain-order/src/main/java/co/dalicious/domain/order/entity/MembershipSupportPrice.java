package co.dalicious.domain.order.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.converter.MonetaryStatusConverter;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order__membership_support_price")
public class MembershipSupportPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @ManyToOne(optional = false)
    @JoinColumn
    @Comment("유저")
    private User user;

    @ManyToOne
    @JoinColumn
    @Comment("그룹")
    private Group group;

    @Column(columnDefinition = "Decimal(15,2) DEFAULT '0.00'")
    @Comment("사용 지원금")
    private BigDecimal usingSupportPrice;

    @Convert(converter = MonetaryStatusConverter.class)
    @Comment("지원금 사용 취소 유무 (1. 차감 2. 환불)")
    private MonetaryStatus monetaryStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "order_item_membership_fk")
    @Comment("지원금 사용 멤버십 결제 내역")
    private OrderItemMembership orderItemMembership;

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

    public MembershipSupportPrice(User user, Group group, BigDecimal usingSupportPrice, MonetaryStatus monetaryStatus, OrderItemMembership orderItemMembership) {
        this.user = user;
        this.group = group;
        this.usingSupportPrice = usingSupportPrice;
        this.monetaryStatus = monetaryStatus;
        this.orderItemMembership = orderItemMembership;
    }
}
