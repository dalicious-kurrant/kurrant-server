package co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MySpotZoneApplicationFormRequestDto {
    private CreateAddressRequestDto address;
    private String mySpotName;
    @NotNull
    private String jibunAddress;
    private String phone;
    private String memo;
}
