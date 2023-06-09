package co.dalicious.domain.address.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateAddressRequestDto {
    @NotNull
    private String zipCode;
    private String address1;
    @NotNull
    private String address2;
    @NotNull
    private String latitude;
    @NotNull
    private String longitude;
}
