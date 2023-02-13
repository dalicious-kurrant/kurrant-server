package co.dalicious.domain.order.entity;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.enums.PaymentType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Table(name = "order__membership")
public class OrderMembership extends Order{

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_id")
    private Membership membership;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private CreditCardInfo creditCardInfo;

    @Builder
    public OrderMembership(String code, PaymentType paymentType, OrderType orderType, CreditCardInfo creditCardInfo) {
        super(code, paymentType, orderType);
        this.creditCardInfo = creditCardInfo;
    }
}