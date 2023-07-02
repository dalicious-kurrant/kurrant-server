package co.dalicious.domain.application_form.dto.mySpotZone;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MySpotZoneApplicationFormRequestDto {
    private CreateAddressRequestDto address;
    private String mySpotName;
    private String phone;
    private String memo;
}
