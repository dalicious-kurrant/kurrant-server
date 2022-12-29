package co.dalicious.domain.application_form.dto.corporation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CorporationApplyInfoDto {
    private String corporationName;
    private Integer employeeCount;
    private String startDate;

    @Builder
    public CorporationApplyInfoDto(String corporationName, Integer employeeCount, String startDate) {
        this.corporationName = corporationName;
        this.employeeCount = employeeCount;
        this.startDate = startDate;
    }
}
