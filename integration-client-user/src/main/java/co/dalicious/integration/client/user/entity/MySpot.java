package co.dalicious.integration.client.user.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientType;
import lombok.AccessLevel;
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
public class MySpot extends UserSpot {

    @Embedded
    @Comment("주소")
    private Address address;

    @Column(name = "name")
    @Comment("이름")
    private String name;

    @Column(name = "is_active")
    @Comment("마이 스팟 상태 - 0 : 비활성 / 1: 활성")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "my_spot_zone_fk")
    private MySpotZone mySpotZone;

    public MySpot(User user, ClientType clientType, Spot spot, Boolean isDefault, Address address, MySpotZone mySpotZone, String name, Boolean isActive) {
        super(user, clientType, spot, isDefault);
        this.address = address;
        this.mySpotZone = mySpotZone;
        this.name = name;
        this.isActive = isActive;
    }

    public void updateMySpotZone(MySpotZone mySpotZone) {
        this.mySpotZone = mySpotZone;
    }

    public void updateActive(Boolean active) {
        this.isActive = active;
    }

    public void updateMySpotForDelete() {
        this.mySpotZone = null;
        this.isActive = false;
        this.address = null;
    }
}
