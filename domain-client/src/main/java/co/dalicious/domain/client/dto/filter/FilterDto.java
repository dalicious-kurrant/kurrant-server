package co.dalicious.domain.client.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterDto {

    private List<String> name;
    private List<String> cityInfos;
    private List<String> countyInfos;
    private List<String> villageInfos;
    private List<String> zipcodeInfos;
    private List<FilterStatusDto> status;

    public FilterDto(List<String> name, List<String> cityInfos, List<String> countyInfos, List<String> villageInfos, List<String> zipcodeInfos, List<FilterStatusDto> status) {
        this.name = name;
        this.cityInfos = cityInfos;
        this.countyInfos = countyInfos;
        this.villageInfos = villageInfos;
        this.zipcodeInfos = zipcodeInfos;
        this.status = status;
    }
}
