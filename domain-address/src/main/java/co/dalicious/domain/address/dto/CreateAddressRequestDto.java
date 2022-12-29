package co.dalicious.domain.address.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;



@Getter
@NoArgsConstructor
public class CreateAddressRequestDto {
    private String zipCode;
    private String address1;
    private String address2;
    private String latitude;
    private String longitude;

    @Builder
    public CreateAddressRequestDto(String zipCode, String address1, String address2, String latitude, String longitude) {
        this.zipCode = zipCode;
        this.address1 = address1;
        this.address2 = address2;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
