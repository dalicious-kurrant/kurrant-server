package co.dalicious.domain.address.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "client__region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("지역 PK")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(name = "zipcodes")
    @Comment("우편 번호")
    private String zipcode;

    @Column(name = "city")
    @Comment("시/도")
    private String city;

    @Column(name = "county")
    @Comment("시/군/구")
    private String county;

    @Column(name = "village")
    @Comment("동/읍/리")
    private String village;

    @Builder
    public Region(String zipcode, String city, String county, String village) {
        this.zipcode = zipcode;
        this.city = city;
        this.county = county;
        this.village = village;
    }

}
