package co.dalicious.domain.user.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.RequestedMySpotZones;
import lombok.AccessLevel;
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
@Getter
@Table(name = "user__my_spot")
public class MySpot extends UserSpot{

    @Embedded
    @Comment("주소")
    private Address address;

    @Column(name = "ho")
    @Comment("호수")
    private Integer ho;

    @Column(name = "메모")
    @Comment("memo")
    private String memo;

    @ManyToOne
    @JoinColumn(name = "my_spot_zone_fk")
    private MySpotZone mySpotZone;

    @ManyToOne
    @JoinColumn(name = "requested_my_spot_zones_fk")
    private RequestedMySpotZones requestedMySpotZones;


}
