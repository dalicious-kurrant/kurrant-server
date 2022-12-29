package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorporationSpotRequestDto {
    private String spotName;
    private CreateAddressRequestDto address;
    private Integer diningType;
}
