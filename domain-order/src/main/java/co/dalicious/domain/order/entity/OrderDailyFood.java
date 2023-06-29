package co.dalicious.domain.order.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.payment.converter.PaymentCompanyConverter;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PaymentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order__daily_food")
public class OrderDailyFood extends Order{
    @Column(columnDefinition = "DECIMAL(15, 2)")
    private BigDecimal totalDeliveryFee;

    @Convert(converter = PaymentCompanyConverter.class)
    @Comment("결제 타입 저장")
    private PaymentCompany paymentCompany;
    @Comment("그룹명")
    private String groupName;

    @Comment("스팟명")
    private String spotName;

    @Comment("상세주소")
    private String ho;

    @Comment("배송 메모")
    private String memo;

    @Comment("전화번호")
    private String phone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Spot spot;

    public void updateOrderUserInfo(OrderUserInfoDto orderUserInfoDto) {
        super.updateOrderUserInfo(orderUserInfoDto);
        this.groupName = orderUserInfoDto.getGroupName();
        this.spotName = orderUserInfoDto.getSpotName();
        this.ho = orderUserInfoDto.getSpotName();
    }

    public OrderDailyFood(String code, OrderType orderType, Address address, PaymentType paymentType, User user, String receiptUrl, BigDecimal totalDeliveryFee, String groupName, String spotName, String ho, Spot spot) {
        super(orderType, code, address, paymentType, receiptUrl, user);
        this.totalDeliveryFee = totalDeliveryFee;
        this.groupName = groupName;
        this.spotName = spotName;
        this.ho = ho;
        this.spot = spot;
    }

    public void updateTotalDeliveryFee(BigDecimal totalDeliveryFee) {
        this.totalDeliveryFee = totalDeliveryFee;
    }

    public void updatePaymentCompany(PaymentCompany paymentCompany) {
        this.paymentCompany = paymentCompany;
    }


    public void updateOrderDailyFoodAfterPayment(String receiptUrl, String paymentKey, String code, PaymentCompany paymentCompany) {
        super.updateOrderAfterPayment(receiptUrl, paymentKey, code);
        this.paymentCompany = paymentCompany;
    }
}