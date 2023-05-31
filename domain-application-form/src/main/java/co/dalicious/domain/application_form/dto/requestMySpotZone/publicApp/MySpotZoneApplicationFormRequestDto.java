package co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySpotZoneApplicationFormRequestDto {
    private ApplyUserDto user;
    private CreateAddressRequestDto address;
    private String mySpotName;
    private String ho;
    private String jibunAddress;
    private String memo;
}
