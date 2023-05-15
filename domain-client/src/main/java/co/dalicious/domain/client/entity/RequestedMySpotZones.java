package co.dalicious.domain.client.entity;

import lombok.AccessLevel;
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

    @Column(name = "zipcode")
    @Comment("우편 번호")
    private String zipcode;

    @Column(name = "city")
    @Comment("시/도")
    private String city;

    @Column(name = "county")
    @Comment("군/구")
    private String county;

    @Column(name = "village")
    @Comment("동/읍/리")
    private String village;

    @Column(name = "waiting_user_count")
    @Comment("신청 유저 수")
    private Integer waitingUserCount;
}
