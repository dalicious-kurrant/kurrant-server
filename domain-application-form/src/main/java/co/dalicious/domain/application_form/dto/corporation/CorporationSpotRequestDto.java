package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CorporationSpotRequestDto {
    private String spotName;
    private CreateAddressRequestDto address;
    private List<Integer> diningTypes;
}
