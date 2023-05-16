package co.dalicious.domain.user.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.RequestedMySpotZones;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.user.entity.enums.ClientType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@DynamicInsert
@DynamicUpdate
@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user__my_spot")
public class MySpot extends UserSpot{

    @Embedded
    @Comment("주소")
    private Address address;

    @Column(name = "ho")
    @Comment("호수")
    private Integer ho;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @ManyToOne
    @JoinColumn(name = "my_spot_zone_fk")
    private MySpotZone mySpotZone;

    @ManyToOne
    @JoinColumn(name = "requested_my_spot_zones_fk")
    private RequestedMySpotZones requestedMySpotZones;

    public MySpot(User user, ClientType clientType, Spot spot, Boolean isDefault, Address address, Integer ho, String memo, MySpotZone mySpotZone, RequestedMySpotZones requestedMySpotZones) {
        super(user, clientType, spot, isDefault);
        this.address = address;
        this.ho = ho;
        this.memo = memo;
        this.mySpotZone = mySpotZone;
        this.requestedMySpotZones = requestedMySpotZones;
    }

    public void updateRequestedMySpotZones(RequestedMySpotZones requestedMySpotZones) {
        this.requestedMySpotZones = requestedMySpotZones;
    }

    public void updateMySpotZone(MySpotZone mySpotZone) {
        this.mySpotZone = mySpotZone;
    }
}
