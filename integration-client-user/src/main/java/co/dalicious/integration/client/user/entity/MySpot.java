package co.dalicious.integration.client.user.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
@Table(name = "user__my_spot")
public class MySpot extends Spot {

    @Column(name = "is_delete")
    @Comment("마이 스팟 삭제 여부 - 1: 삭제")
    private Boolean isDelete;

    @Column(name = "user_Id")
    @Comment("유저 Id")
    private BigInteger userId;

    @Builder
    public MySpot(String name, Address address, List<DiningType> diningTypes, Group group, String memo, Boolean isDelete, BigInteger userId) {
        super(name, address, diningTypes, group, memo);
        this.isDelete = isDelete;
        this.userId = userId;
    }

    public void updateMySpotForDelete() {
        this.updateSpotStatus(SpotStatus.INACTIVE);
        this.isDelete = true;
    }
}
