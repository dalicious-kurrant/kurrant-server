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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                null : createPoint(createAddressRequestDto.getLongitude() + " " + createAddressRequestDto.getLatitude());
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

    public Address(String zipCode, String address1, String address2) throws ParseException {
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;

        Map<String, String> map = AddressUtil.getLocation(address1);
        this.location = createPoint(map.get("location"));
        this.address3 = map.get("jibunAddress");
    }

    public void makeAddress(String address1, String address2, String zipcode, String location) throws ParseException {
        this.address1 = address1;
        this.address2 = address2;
        this.zipCode = zipcode;
        this.location = createPoint(location);
    }

    public String addressToString() {
        if (this.address2 == null || this.address2.isBlank() || this.address2.isEmpty()) {
            return this.address1;
        }
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

    public String stringToAddress3() { return this.address3.replaceFirst(".*?(?:시|군|구)\\s", "").replaceFirst(".*?(?:군|구)\\s", ""); }

    public Map<String, String> getLatitudeAndLongitude() {
        Map<String, String> locationMap = new HashMap<>();
        String[] locationArr = this.locationToString().split(" ");
        locationMap.put("longitude", locationArr[0]);
        locationMap.put("latitude", locationArr[1]);
        return locationMap;
    }

    public void deleteAddress() {
        // 신(구)주소, 도로명 주소
        String regex = "(([가-힣]+(\\d{1,5}|\\d{1,5}(,|.)\\d{1,5}|)+([읍면동가리]))(^구|)((\\d{1,5}([~|-])\\d{1,5}|\\d{1,5})(가|리|)|))( (산(\\d{1,5}([~|-])\\d{1,5}|\\d{1,5}))|)|";
        String newRegx = "(([가-힣]|(\\d{1,5}([~|-])\\d{1,5})|\\d{1,5})+([로길])|(\\d))";

        Matcher matcher = Pattern.compile(regex).matcher(this.address3);
        Matcher newMatcher = Pattern.compile(newRegx).matcher(this.address1);

        if(matcher.find()) {
            this.address3 = matcher.group().replaceAll("[0-9]", "*");
        } else if(newMatcher.find()) {
            this.address1 = matcher.group().replaceAll("[0-9]", "*");
        }
    }

}
