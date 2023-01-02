package co.dalicious.domain.application_form.dto.corporation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
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
