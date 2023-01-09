package co.dalicious.domain.address.dto;

import co.dalicious.domain.address.entity.embeddable.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressResponseDto {
    private String address;

    public CreateAddressResponseDto(Address address) {
        this.address = address.getAddress1() + " " + address.getAddress2();
    }
}
