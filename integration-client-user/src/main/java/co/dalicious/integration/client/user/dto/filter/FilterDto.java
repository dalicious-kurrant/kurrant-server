package co.dalicious.integration.client.user.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterDto {

    private List<FilterInfo> name;
    private List<FilterInfo> cityInfos;
    private List<FilterInfo> countyInfos;
    private List<FilterInfo> villageInfos;
    private List<FilterInfo> zipcodeInfos;
    private List<FilterStatusDto> status;

    public FilterDto(List<FilterInfo> name, List<FilterInfo> cityInfos, List<FilterInfo> countyInfos, List<FilterInfo> villageInfos, List<FilterInfo> zipcodeInfos, List<FilterStatusDto> status) {
        this.name = name;
        this.cityInfos = cityInfos;
        this.countyInfos = countyInfos;
        this.villageInfos = villageInfos;
        this.zipcodeInfos = zipcodeInfos;
        this.status = status;
    }
}
