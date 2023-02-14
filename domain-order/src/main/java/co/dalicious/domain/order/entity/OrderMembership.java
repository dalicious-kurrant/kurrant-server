package co.dalicious.domain.order.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PaymentType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Table(name = "order__membership")
public class OrderMembership extends Order{

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private Membership membership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private CreditCardInfo creditCardInfo;

    @Builder
    public OrderMembership(OrderType orderType, String code, Address address, BigDecimal defaultPrice, BigDecimal point, BigDecimal totalPrice, PaymentType paymentType, String paymentKey, String receiptUrl, User user, Membership membership, CreditCardInfo creditCardInfo) {
        super(orderType, code, address, defaultPrice, point, totalPrice, paymentType, paymentKey, receiptUrl, user);
        this.membership = membership;
        this.creditCardInfo = creditCardInfo;
    }
}