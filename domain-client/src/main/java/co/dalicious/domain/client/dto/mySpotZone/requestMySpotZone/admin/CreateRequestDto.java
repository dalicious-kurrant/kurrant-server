package co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestDto {
    private String zipcode;
    private String city;
    private String county;
    private String village;
    private Integer waitingUserCount;
    private String memo;
}
