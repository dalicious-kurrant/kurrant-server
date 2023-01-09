package co.dalicious.domain.address.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressRequestDto {
    private String zipCode;
    private String address1;
    private String address2;
    private String latitude;
    private String longitude;
}
