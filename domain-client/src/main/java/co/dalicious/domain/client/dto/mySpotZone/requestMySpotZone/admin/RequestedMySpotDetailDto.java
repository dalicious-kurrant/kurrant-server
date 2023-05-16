package co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class RequestedMySpotDetailDto {
    private BigInteger id;
    private String zipcode;
    private String city;
    private String county;
    private String village;
    private Integer requestUserCount;
    private String memo;
}
