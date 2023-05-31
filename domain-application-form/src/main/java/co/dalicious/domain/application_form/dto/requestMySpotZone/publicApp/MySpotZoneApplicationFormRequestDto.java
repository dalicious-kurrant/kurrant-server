package co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySpotZoneApplicationFormRequestDto {
    private CreateAddressRequestDto address;
    private String mySpotName;
    private String jibunAddress;
    private String memo;
    private String phone;
}
