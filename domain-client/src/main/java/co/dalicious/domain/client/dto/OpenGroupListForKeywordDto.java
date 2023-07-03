package co.dalicious.domain.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class OpenGroupListForKeywordDto {
    private BigInteger id;
    private String name;
    private String address;
    private String jibunAddress;
    private String latitude;
    private String longitude;
}
