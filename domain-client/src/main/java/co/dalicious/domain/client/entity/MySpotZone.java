package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.converter.MysSpotZoneStatusConverter;
import co.dalicious.domain.client.dto.mySpotZone.UpdateRequestDto;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "client__my_spot_zone")
public class MySpotZone extends Group{

    @Convert(converter = MysSpotZoneStatusConverter.class)
    @Column(name = "e_my_spot_zone_status")
    @Comment("스팟 오픈 상태")
    private MySpotZoneStatus mySpotZoneStatus;

    @OneToMany(mappedBy = "mySpotZone", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__my_spot_zone_fk")
    @Comment("지역 pk")
    private List<Region> regionList;

    @Column(name = "open_start_date")
    @Comment("오픈 시작일")
    private LocalDate openStartDate;

    @Column(name = "open_close_date")
    @Comment("오픈 마감일")
    private LocalDate openCloseDate;

    @Column(name = "my_spot_zone_user_count")
    @Comment("이용 유저 수")
    private Integer mySpotZoneUserCount;

    @Builder
    public MySpotZone(Address address, List<DiningType> diningTypes, String name, String memo, MySpotZoneStatus mySpotZoneStatus, LocalDate openStartDate, LocalDate openCloseDate, Integer mySpotZoneUserCount) {
        super(address, diningTypes, name, memo);
        this.mySpotZoneStatus = mySpotZoneStatus;
        this.openStartDate = openStartDate;
        this.openCloseDate = openCloseDate;
        this.mySpotZoneUserCount = mySpotZoneUserCount;
    }

    public void updateMySpotZone(UpdateRequestDto updateRequestDto) {
        List<DiningType> diningTypes = updateRequestDto.getDiningTypes().stream().map(DiningType::ofCode).toList();
        updateGroup(diningTypes, updateRequestDto.getName(), updateRequestDto.getMemo());
        this.mySpotZoneStatus = MySpotZoneStatus.ofCode(updateRequestDto.getStatus());
        this.openStartDate = DateUtils.stringToDate(updateRequestDto.getOpenStartDate());
        this.openCloseDate = DateUtils.stringToDate(updateRequestDto.getOpenCloseDate());
    }
}
