package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.Region;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "client__requested_my_spot_zones")
public class RequestedMySpotZones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("신청한 마이 스팟존 PK")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "region_fk")
    private Region region;

    @Column(name = "waiting_user_count")
    @Comment("신청 유저 수")
    private Integer waitingUserCount;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @Builder
    public RequestedMySpotZones(Region region, Integer waitingUserCount, String memo) {
        this.region = region;
        this.waitingUserCount = waitingUserCount;
        this.memo = memo;
    }

    public void updateRequestedMySpotZones(RequestedMySpotDetailDto updateRequestDto, Region region) {
        this.region = region;
        this.waitingUserCount = updateRequestDto.getRequestUserCount();
        this.memo = updateRequestDto.getMemo();
    }

    public void updateWaitingUserCount(Integer count) {
        this.waitingUserCount = this.waitingUserCount + count;
    }

}
