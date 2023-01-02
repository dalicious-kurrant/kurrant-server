package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@NoArgsConstructor
public class ApartmentResponseDto {
    private BigInteger id;
    private String name;
    private String address;

    @Builder
    public ApartmentResponseDto(BigInteger id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
