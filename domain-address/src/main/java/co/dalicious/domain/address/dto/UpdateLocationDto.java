package co.dalicious.domain.address.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class UpdateLocationDto {
    private String location;
    private BigInteger id;
}
