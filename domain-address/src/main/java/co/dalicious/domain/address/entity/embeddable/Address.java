package co.dalicious.domain.address.entity.embeddable;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTReader;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Address {
  @Column(name = "zip_code", nullable = false, columnDefinition = "MEDIUMINT COMMENT '우편번호, 다섯자리'")
  private String zipCode;

  @Column(name = "address_depth_1", nullable = true, columnDefinition = "VARCHAR(255) COMMENT '기본주소'")
  private String address1;

  @Column(name = "address_depth_2", nullable = true, columnDefinition = "VARCHAR(255) COMMENT '상세주소'")
  private String address2;

  @Column(name = "address_location", nullable = true)
  @Comment("위치")
  private Point location;

  @Builder
  public Address(CreateAddressRequestDto createAddressRequestDto) throws ParseException {
    this.zipCode = createAddressRequestDto.getZipCode();
    this.address1 = createAddressRequestDto.getAddress1();
    this.address2 = createAddressRequestDto.getAddress2();
    this.location = (createAddressRequestDto.getLatitude() == null || createAddressRequestDto.getLongitude() == null) ?
            null : createPoint(Double.valueOf(createAddressRequestDto.getLatitude()), Double.valueOf(createAddressRequestDto.getLongitude()));
  }

  public Address(String zipCode, String address1, String address2, Point location) {
    this.zipCode = zipCode;
    this.address1 = address1;
    this.address2 = address2;
    this.location = location;
  }

  public String addressToString() {
    return this.address1 + " " + this.address2;
  }

  public static Point createPoint(Double x, Double y)  {
    GeometryFactory geometryFactory = new GeometryFactory();
    return geometryFactory.createPoint(new Coordinate(x,y));
  }
}
