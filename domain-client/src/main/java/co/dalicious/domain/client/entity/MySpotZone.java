package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.converter.CountriesConverter;
import co.dalicious.domain.client.converter.MysSpotZoneStatusConverter;
import co.dalicious.domain.client.converter.VillagesConverter;
import co.dalicious.domain.client.converter.ZipcodesConverter;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
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

    @Convert(converter = ZipcodesConverter.class)
    @Column(name = "zipcodes")
    @Comment("우편 번호")
    private List<String> zipcodes;

    @Column(name = "open_start_date")
    @Comment("오픈 시작일")
    private LocalDate openStartDate;

    @Column(name = "open_close_date")
    @Comment("오픈 마감일")
    private LocalDate openCloseDate;

    @Column(name = "city")
    @Comment("시/도")
    private String city;

    @Convert(converter = CountriesConverter.class)
    @Column(name = "coutries")
    @Comment("군/구")
    private List<String> countries;

    @Convert(converter = VillagesConverter.class)
    @Column(name = "villages")
    @Comment("동/읍/리")
    private List<String> villages;

    @Column(name = "my_spot_zone_user_count")
    @Comment("이용 유저 수")
    private Integer mySpotZoneUserCount;

    @Builder
    public MySpotZone(Address address, List<DiningType> diningTypes, String name, String memo, MySpotZoneStatus mySpotZoneStatus, List<String> zipcodes, LocalDate openStartDate, LocalDate openCloseDate, String city, List<String> countries, List<String> villages, Integer mySpotZoneUserCount) {
        super(address, diningTypes, name, memo);
        this.mySpotZoneStatus = mySpotZoneStatus;
        this.zipcodes = zipcodes;
        this.openStartDate = openStartDate;
        this.openCloseDate = openCloseDate;
        this.city = city;
        this.countries = countries;
        this.villages = villages;
        this.mySpotZoneUserCount = mySpotZoneUserCount;
    }
}
