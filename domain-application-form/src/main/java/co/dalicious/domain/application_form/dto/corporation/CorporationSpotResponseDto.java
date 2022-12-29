package co.dalicious.domain.application_form.dto.corporation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorporationSpotResponseDto {
    private String spotName;
    private String address;
    private String diningType;

    @Builder
    public CorporationSpotResponseDto(String spotName, String address, String diningType) {
        this.spotName = spotName;
        this.address = address;
        this.diningType = diningType;
    }
}
