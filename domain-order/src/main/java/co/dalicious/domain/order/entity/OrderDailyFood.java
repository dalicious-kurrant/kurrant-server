package co.dalicious.domain.order.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
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

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order__daily_food")
public class OrderDailyFood extends Order{

    @Comment("그룹명")
    private String groupName;

    @Comment("스팟명")
    private String spotName;

    @Comment("상세주소")
    private String ho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Spot spot;

    public void updateOrderUserInfo(OrderUserInfoDto orderUserInfoDto) {
        super.updateOrderUserInfo(orderUserInfoDto);
        this.groupName = orderUserInfoDto.getGroupName();
        this.spotName = orderUserInfoDto.getSpotName();
        this.ho = orderUserInfoDto.getSpotName();
    }

    public OrderDailyFood(String code, Address address, PaymentType paymentType, User user, String groupName, String spotName, String ho, Spot spot) {
        super(code, address, paymentType, user);
        this.groupName = groupName;
        this.spotName = spotName;
        this.ho = ho;
        this.spot = spot;
    }
}