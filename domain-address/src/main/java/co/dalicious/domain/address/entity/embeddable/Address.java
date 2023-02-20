package co.dalicious.domain.address.entity.embeddable;

import java.sql.Timestamp;
import javax.persistence.*;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.geo.Point;

@Getter
@Embeddable
@NoArgsConstructor
public class Address {
  @Column(name = "zip_code", nullable = false, columnDefinition = "MEDIUMINT COMMENT '우편번호, 다섯자리'")
  private Integer zipCode;

  @Column(name = "address_depth_1", nullable = true, columnDefinition = "VARCHAR(255) COMMENT '기본주소'")
  private String address1;

  @Column(name = "address_depth_2", nullable = true, columnDefinition = "VARCHAR(255) COMMENT '상세주소'")
  private String address2;

  @Column(name = "address_location", nullable = true)
  @Comment("위치")
  private Point location;

  @Builder
  public Address(CreateAddressRequestDto createAddressRequestDto) {
    this.zipCode = Integer.parseInt(createAddressRequestDto.getZipCode());
    this.address1 = createAddressRequestDto.getAddress1();
    this.address2 = createAddressRequestDto.getAddress2();
    this.location = (createAddressRequestDto.getLatitude() == null || createAddressRequestDto.getLongitude() == null) ?
            null : new Point(Double.parseDouble(createAddressRequestDto.getLatitude()), Double.parseDouble(createAddressRequestDto.getLongitude()));
  }

  public String addressToString() {
    return this.address1 + " " + this.address2;
  }
}
