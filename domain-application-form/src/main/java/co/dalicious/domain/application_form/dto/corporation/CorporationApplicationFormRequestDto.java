package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "기업 스팟 개설 요청 DTO")
@Getter
@NoArgsConstructor
public class CorporationApplicationFormRequestDto {
    private ApplyUserDto user;
    private CorporationApplyInfoDto corporationApplyInfoDto;
    private CreateAddressRequestDto createAddressRequestDto;
    private List<CorporationMealInfoRequestDto> mealDetails;
    private List<CorporationSpotApplicationFormDto> spots;
    private CorporationOptionsApplicationFormRequestDto option;
}
