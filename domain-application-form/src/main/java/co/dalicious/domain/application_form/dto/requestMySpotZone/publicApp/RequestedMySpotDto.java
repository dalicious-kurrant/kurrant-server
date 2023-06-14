package co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestedMySpotDto {
    private Boolean isRequested;
    private String address;
}
