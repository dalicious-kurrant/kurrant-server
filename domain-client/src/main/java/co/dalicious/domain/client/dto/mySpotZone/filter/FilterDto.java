package co.dalicious.domain.client.dto.mySpotZone.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterDto {

    private List<CityInfo> cityInfos;
    private List<CountyInfo> countyInfos;
    private List<VillageInfo> villageInfos;
    private List<ZipcodeInfo> zipcodeInfos;
}
