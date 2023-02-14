package co.dalicious.domain.order.entity;

import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.Membership;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order__order_item_membership")
public class OrderItemMembership extends OrderItem{
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Comment("멤버십 구독 타입")
    private String membershipSubscriptionType;

    @Column(name = "price", columnDefinition="Decimal(15,2) default '0.00'")
    @Comment("상품 가격")
    private BigDecimal price;

    @Column(name = "discount_price", columnDefinition="Decimal(15,2) default '0.00'")
    @Comment("할인한 가격")
    private BigDecimal discountPrice;

    @Column(name = "period_discounted_rate", columnDefinition="Decimal(15,2) default '0.00'")
    @Comment("기간 할인율")
    private Integer periodDiscountedRate;

    @OneToMany(mappedBy = "orderItemMembership")
    @JsonBackReference(value = "order_item_membership_fk")
    @Comment("멤버십 지원금")
    private List<MembershipSupportPrice> membershipSupportPriceList;

    @Builder
    public OrderItemMembership(OrderStatus orderStatus, Order order, Membership membership, String membershipSubscriptionType, BigDecimal price, BigDecimal discountPrice, Integer periodDiscountedRate) {
        super(orderStatus, order);
        this.membership = membership;
        this.membershipSubscriptionType = membershipSubscriptionType;
        this.price = price;
        this.discountPrice = discountPrice;
        this.periodDiscountedRate = periodDiscountedRate;
    }

    public void updateDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }
}
