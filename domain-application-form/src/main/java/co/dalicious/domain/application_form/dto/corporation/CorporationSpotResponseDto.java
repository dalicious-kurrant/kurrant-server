package co.dalicious.domain.application_form.dto.corporation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "기업 스팟 개설 신청 스팟 정보 응답 DTO")
public class CorporationSpotResponseDto {
    private String spotName;
    private String address;
    private List<String> diningTypes;

    @Builder
    public CorporationSpotResponseDto(String spotName, String address, List<String> diningTypes) {
        this.spotName = spotName;
        this.address = address;
        this.diningTypes = diningTypes;
    }
}
