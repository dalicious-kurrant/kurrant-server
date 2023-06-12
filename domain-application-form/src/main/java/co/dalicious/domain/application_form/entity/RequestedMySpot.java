package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__requested_my_spot")
public class RequestedMySpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("신청한 마이 스팟 PK")
    private BigInteger id;

    @Column(name = "user_Id")
    @Comment("유저 Id")
    private BigInteger userId;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("스팟 주소")
    private Address address;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    @Comment("스팟 이름")
    private String name;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_form__requested_my_spot_zones_id")
    @JsonManagedReference(value = "application_form__requested_my_spot_zones_fk")
    @Comment("신청 마이스팟 존 ID")
    private RequestedMySpotZones requestedMySpotZones;

    @Builder
    public RequestedMySpot(BigInteger userId, Address address, String name, String memo, RequestedMySpotZones requestedMySpotZones) {
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.memo = memo;
        this.requestedMySpotZones = requestedMySpotZones;
    }
}
