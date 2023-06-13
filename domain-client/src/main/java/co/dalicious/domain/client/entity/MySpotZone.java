package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.converter.MySpotZoneStatusConverter;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "client__my_spot_zone")
public class MySpotZone extends Group {

    @Convert(converter = MySpotZoneStatusConverter.class)
    @Column(name = "e_my_spot_zone_status")
    @Comment("스팟 오픈 상태")
    private MySpotZoneStatus mySpotZoneStatus;

    @Column(name = "open_start_date")
    @Comment("오픈 예약일")
    private LocalDate openDate;

    @Column(name = "open_close_date")
    @Comment("중지 예약일")
    private LocalDate closeDate;

    @Column(name = "my_spot_zone_user_count")
    @Comment("이용 유저 수")
    private Integer mySpotZoneUserCount;

    @Builder
    public MySpotZone(Address address, List<DiningType> diningTypes, String name, String memo, MySpotZoneStatus mySpotZoneStatus, LocalDate openDate, LocalDate closeDate, Integer mySpotZoneUserCount) {
        super(address, diningTypes, name, memo);
        this.mySpotZoneStatus = mySpotZoneStatus;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.mySpotZoneUserCount = mySpotZoneUserCount;
    }

    public void updateMySpotZoneUserCount(Integer count) {
        this.mySpotZoneUserCount = this.mySpotZoneUserCount + count;
    }
}
