package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "기업 스팟 개설 요청 DTO")
@Getter
@Setter
public class CorporationApplicationFormRequestDto {
    private ApplyUserDto user;
    private CorporationApplyInfoDto corporationInfo;
    private CreateAddressRequestDto address;
    private List<CorporationMealInfoRequestDto> mealDetails;
    private List<CorporationSpotRequestDto> spots;
    private CorporationOptionsDto option;
}
