package co.dalicious.domain.application_form.dto.corporation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorporationOptionsApplicationFormRequestDto {
    private Boolean isGarbage;
    private Boolean isHotStorage;
    private Boolean isSetting;
    private String memo;
}
