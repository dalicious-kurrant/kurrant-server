package co.dalicious.domain.address.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAddressResponseDto {
    private String address;

    @Builder
    public CreateAddressResponseDto(String address1, String address2) {
        this.address = address1 + " " + address2;
    }
}
