package co.dalicious.domain.application_form.dto.corporation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorporationOptionsApplicationFormRequestDto {
    private Boolean isGarbage;
    private Boolean isHotStorage;
    private Boolean isSetting;
    private String memo;

    @Builder
    public CorporationOptionsApplicationFormRequestDto(Boolean isGarbage, Boolean isHotStorage, Boolean isSetting, String memo) {
        this.isGarbage = isGarbage;
        this.isHotStorage = isHotStorage;
        this.isSetting = isSetting;
        this.memo = memo;
    }
}
