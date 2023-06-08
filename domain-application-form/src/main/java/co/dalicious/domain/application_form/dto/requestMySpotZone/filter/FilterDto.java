package co.dalicious.domain.application_form.dto.requestMySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterDto {

    private List<FilterInfo> cityInfos;
    private List<FilterInfo> countyInfos;
    private List<FilterInfo> villageInfos;
    private List<FilterInfo> zipcodeInfos;
}
