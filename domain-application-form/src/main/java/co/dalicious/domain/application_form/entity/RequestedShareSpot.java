package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.converter.ShareSpotRequestTypeConverter;
import co.dalicious.domain.application_form.entity.enums.ShareSpotRequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.io.ParseException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__requested_share_spot")
public class RequestedShareSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("신청한 공유 스팟 PK")
    private BigInteger id;

    @Convert(converter = ShareSpotRequestTypeConverter.class)
    @Comment("공유 스팟 신청 타입 1.개설 2.추가 3.시간추가")
    private ShareSpotRequestType shareSpotRequestType;

    @NotNull
    @Column(name = "emb_address")
    @Comment("주소")
    private Address address;

    @Comment("신청 유저 id")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger userId;

    @Comment("그룹 id")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger groupId;

    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @Comment("외부인 출입 가능 여부")
    private Boolean entranceOption;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

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

    public RequestedShareSpot(ShareSpotRequestType shareSpotRequestType, CreateAddressRequestDto address, BigInteger userId, BigInteger groupId, LocalTime deliveryTime, Boolean entranceOption, String memo) throws ParseException {
        this.shareSpotRequestType = shareSpotRequestType;
        this.address = new Address(address);
        this.userId = userId;
        this.groupId = groupId;
        this.deliveryTime = deliveryTime;
        this.entranceOption = entranceOption;
        this.memo = memo;
    }

    public void updateShareSpotRequestType(ShareSpotRequestType shareSpotRequestType) {
        this.shareSpotRequestType = shareSpotRequestType;
    }

    public void updateUserId(BigInteger userId) {
        this.userId = userId;
    }

    public void setShareSpotRequestType(ShareSpotRequestType shareSpotRequestType) {
        this.shareSpotRequestType = shareSpotRequestType;
    }

    public void setAddress(CreateAddressRequestDto createAddressRequestDto) throws ParseException {
        this.address = new Address(createAddressRequestDto);
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public void setDeliveryTime(LocalTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setEntranceOption(Boolean entranceOption) {
        this.entranceOption = entranceOption;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
