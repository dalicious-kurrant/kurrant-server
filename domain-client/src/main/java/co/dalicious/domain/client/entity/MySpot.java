package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MySpot extends Spot {

    @Column(name = "is_delete", columnDefinition = "boolean default false")
    @Comment("마이 스팟 삭제 여부 - 1: 삭제")
    private Boolean isDelete;

    @Column(name = "user_Id")
    @Comment("유저 Id")
    private BigInteger userId;

    @Column(name = "is_alarm", columnDefinition = "boolean default false")
    @Comment("마이 스팟 푸시알림 여부 - 1: 수신")
    private Boolean isAlarm;

    @Builder
    public MySpot(String name, Address address, List<DiningType> diningTypes, Group group, String memo, Boolean isDelete, BigInteger userId, Boolean isAlarm) {
        super(name, address, diningTypes, group, memo);
        this.isDelete = isDelete;
        this.userId = userId;
        this.isAlarm = isAlarm;
    }

    public void updateMySpotForDelete() {
        this.updateSpotStatus(SpotStatus.INACTIVE);
        this.isDelete = true;
        if(this.getGroup() instanceof MySpotZone mySpotZone) mySpotZone.updateMySpotZoneUserCount(1, SpotStatus.INACTIVE);

        this.getAddress().deleteAddress();
    }

    public void updateAlarm(Boolean alarm) {
        this.isAlarm = alarm;
    }
}
