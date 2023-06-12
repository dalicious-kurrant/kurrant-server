package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.integration.client.user.entity.Region;
import co.dalicious.system.converter.IdListConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__requested_my_spot_zones")
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

    @OneToMany(mappedBy = "requestedMySpotZones", fetch = FetchType.LAZY)
    @JsonBackReference(value = "application_form__requested_my_spo_fk")
    @Comment("신청 마이 스팟 리스트")
    List<RequestedMySpot> requestedMySpots;

    @Convert(converter = IdListConverter.class)
    @Comment("푸시 알림 신청 유저 ID 리스트")
    private List<BigInteger> pushAlarmUserIds;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Builder
    public RequestedMySpotZones(Region region, Integer waitingUserCount, String memo, List<BigInteger> pushAlarmUserIds) {
        this.region = region;
        this.waitingUserCount = waitingUserCount;
        this.memo = memo;
        this.pushAlarmUserIds = pushAlarmUserIds;
    }

    public void updateRequestedMySpotZones(RequestedMySpotDetailDto updateRequestDto, Region region) {
        this.region = region;
        this.waitingUserCount = updateRequestDto.getRequestUserCount();
        this.memo = updateRequestDto.getMemo();
    }

    public void updateWaitingUserCount(Integer count) {
        this.waitingUserCount = this.waitingUserCount + count;
    }

    public void updatePushAlarmUserIds(List<BigInteger> userIds) { this.pushAlarmUserIds = userIds; }
}
