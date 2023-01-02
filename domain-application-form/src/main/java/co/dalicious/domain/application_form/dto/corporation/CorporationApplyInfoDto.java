package co.dalicious.domain.application_form.dto.corporation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CorporationApplyInfoDto {
    private String corporationName;
    private Integer employeeCount;
    private String startDate;
    private List<String> diningTypes;

    @Builder
    public CorporationApplyInfoDto(String corporationName, Integer employeeCount, String startDate, List<String> diningTypes) {
        this.corporationName = corporationName;
        this.employeeCount = employeeCount;
        this.startDate = startDate;
        this.diningTypes = diningTypes;
    }
}
