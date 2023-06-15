package co.dalicious.domain.address.entity.embeddable;

import ch.qos.logback.core.util.LocationUtil;
import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.utils.AddressUtil;
import exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.http.HttpStatus;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigInteger;

@Getter
@Embeddable
@Setter
@NoArgsConstructor
public class Address {
    @Column(name = "zip_code", columnDefinition = "VARCHAR(5) COMMENT '우편번호, 다섯자리'")
    private String zipCode;

    @Column(name = "address_depth_1", columnDefinition = "VARCHAR(255) COMMENT '기본주소'")
    private String address1;

    @Column(name = "address_depth_2", columnDefinition = "VARCHAR(255) COMMENT '상세주소'")
    private String address2;

    @Column(name = "address_depth_3", columnDefinition = "VARCHAR(255) COMMENT '지번주소'")
    private String address3;

    @Column(name = "address_location")
    @Comment("위치")
    private Geometry location;


    public Address(CreateAddressRequestDto createAddressRequestDto) throws ParseException {
        this.zipCode = createAddressRequestDto.getZipCode();
        this.address1 = createAddressRequestDto.getAddress1();
        this.address2 = createAddressRequestDto.getAddress2();
        this.address3 = createAddressRequestDto.getAddress3();
        this.location = (createAddressRequestDto.getLatitude() == null || createAddressRequestDto.getLongitude() == null) ?
                null : createPoint(createAddressRequestDto.getLatitude() + " " + createAddressRequestDto.getLongitude());
    }


    public Address(String zipCode, String address1, String address2, String address3, String location) throws ParseException {
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.location = createPoint(location);
    }

    public Address(String zipCode, String address1, String address2, String location) throws ParseException {
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;
        this.location = createPoint(location);
    }

    public void makeAddress(String address1, String address2, String zipcode, String location) throws ParseException {
        this.address1 = address1;
        this.address2 = address2;
        this.zipCode = zipcode;
        this.location = createPoint(location);
    }

    public String addressToString() {
        return this.address1 + " " + this.address2;
    }

    public static Geometry createPoint(String location) {
        if(location == null) return null;
        WKTReader wktReader = new WKTReader();
        try {
            return wktReader.read("POINT(" + location + ")");
        } catch (ParseException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000014", "Location 저장에 오류가 발생했습니다");
        }
    }

    public void setLocation(String location) {
        this.location = createPoint(location);
    }
    public void updateLocation(String location) throws ParseException { this.location = createPoint(location); }

    public String locationToString() {
    return this.location.toString().replaceAll("POINT |[(]|[)]", "");
  }

    public void updateAddress3(String address3) { this.address3 = address3; }
}
