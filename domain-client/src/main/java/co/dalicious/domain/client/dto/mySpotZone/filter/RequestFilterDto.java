package co.dalicious.domain.client.dto.mySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFilterDto {
    private String city;
    private String county;
    private String village;
    private String zipcode;
}
