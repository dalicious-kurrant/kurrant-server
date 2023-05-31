package co.dalicious.integration.client.user.entity;

import co.dalicious.domain.address.entity.Region;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.integration.client.user.converter.MySpotZoneStatusConverter;
import co.dalicious.integration.client.user.converter.RegionIdsConverter;
import co.dalicious.integration.client.user.dto.mySpotZone.UpdateRequestDto;
import co.dalicious.integration.client.user.entity.enums.MySpotZoneStatus;
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
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__my_spot_zone")
public class MySpotZone extends Group {

    @Convert(converter = MySpotZoneStatusConverter.class)
    @Column(name = "e_my_spot_zone_status")
    @Comment("스팟 오픈 상태")
    private MySpotZoneStatus mySpotZoneStatus;

    @Convert(converter = RegionIdsConverter.class)
    @Column(name = "region_ids")
    @Comment("지역 pk")
    private List<BigInteger> regionIds;

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
    public MySpotZone(Address address, List<DiningType> diningTypes, String name, String memo, MySpotZoneStatus mySpotZoneStatus, List<BigInteger> regionIds, LocalDate openStartDate, LocalDate openCloseDate, Integer mySpotZoneUserCount) {
        super(address, diningTypes, name, memo);
        this.mySpotZoneStatus = mySpotZoneStatus;
        this.regionIds = regionIds;
        this.openStartDate = openStartDate;
        this.openCloseDate = openCloseDate;
        this.mySpotZoneUserCount = mySpotZoneUserCount;
    }

    public void updateMySpotZone(UpdateRequestDto updateRequestDto) {
        List<DiningType> diningTypes = updateRequestDto.getDiningTypes().stream().map(DiningType::ofCode).toList();
        this.updateGroup(diningTypes, updateRequestDto.getName(), updateRequestDto.getMemo());
        this.mySpotZoneStatus = MySpotZoneStatus.ofCode(updateRequestDto.getStatus());
        this.openStartDate = DateUtils.stringToDate(updateRequestDto.getOpenStartDate());
        this.openCloseDate = DateUtils.stringToDate(updateRequestDto.getOpenCloseDate());
    }

    public void updateMySpotZoneUserCount(Integer count) {
        this.mySpotZoneUserCount = this.mySpotZoneUserCount + count;
    }

    public void updateRegionIds(List<BigInteger> regionIds) {
        this.regionIds = regionIds;
    }
}
