package co.dalicious.domain.address.entity.embeddable;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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

  @Column(name = "address_location")
  @Comment("위치")
  private Geometry location;

  @Builder
  public Address(CreateAddressRequestDto createAddressRequestDto) throws ParseException {
    this.zipCode = createAddressRequestDto.getZipCode();
    this.address1 = createAddressRequestDto.getAddress1();
    this.address2 = createAddressRequestDto.getAddress2();
    this.location = (createAddressRequestDto.getLatitude() == null || createAddressRequestDto.getLongitude() == null) ?
            null : createPoint(createAddressRequestDto.getLatitude() + " " + createAddressRequestDto.getLongitude());
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

  public static Geometry createPoint(String location) throws ParseException {
    WKTReader wktReader = new WKTReader();
    if(location == null) return null;
    return wktReader.read("POINT("+location+")");
  }

}
