package co.dalicious.domain.address.dto;

import co.dalicious.domain.address.entity.embeddable.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressResponseDto {
    private String address1;
    private String address2;

    public CreateAddressResponseDto(Address address) {
        this.address1 = address.getAddress1();
        this.address2 = address.getAddress2();
    }
}
