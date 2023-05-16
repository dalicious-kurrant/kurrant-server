package co.dalicious.domain.client.dto.mySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterDto {

    private List<String> cityInfos;
    private List<String> countyInfos;
    private List<String> villageInfos;
    private List<String> zipcodeInfos;
}
