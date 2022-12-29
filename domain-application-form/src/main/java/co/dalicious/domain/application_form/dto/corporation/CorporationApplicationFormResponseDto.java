package co.dalicious.domain.application_form.dto.corporation;

import co.dalicious.domain.application_form.dto.ApplyUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "기업 스팟 개설 응답 DTO")
@Getter
@NoArgsConstructor
public class CorporationApplicationFormResponseDto {
    private ApplyUserDto user;
    private CorporationApplyInfoDto corporationInfo;
    private String address;
    private List<CorporationMealInfoResponseDto> mealDetails;
    private List<CorporationSpotResponseDto> spots;
    private CorporationOptionsDto option;

    @Builder
    public CorporationApplicationFormResponseDto(ApplyUserDto user, CorporationApplyInfoDto corporationInfo, String address, List<CorporationMealInfoResponseDto> mealDetails, List<CorporationSpotResponseDto> spots, CorporationOptionsDto option) {
        this.user = user;
        this.corporationInfo = corporationInfo;
        this.address = address;
        this.mealDetails = mealDetails;
        this.spots = spots;
        this.option = option;
    }
}
