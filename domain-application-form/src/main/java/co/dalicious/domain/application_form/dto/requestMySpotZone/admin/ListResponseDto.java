package co.dalicious.domain.application_form.dto.requestMySpotZone.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ListResponseDto {
    private BigInteger id;
    private String city;
    private String county;
    private String village;
    private String zipcode;
    private Integer requestUserCount;
    private String memo;
}
