package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.mySpotZone.filter.*;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RequestedMySpotZonesMapper {

    default FilterDto toFilterDto(Set<String> citySet, Set<String> countySet, Set<String> villageSet, Set<String> zipcodeSet) {
        List<CityInfo> cityInfoList = citySet.stream().map(this::toCityInfo).toList();
        List<CountyInfo> countyInfoList = countySet.stream().map(this::toCountyInfo).toList();
        List<VillageInfo> villageInfoList = villageSet.stream().map(this::toVillageInfo).toList();
        List<ZipcodeInfo> zipcodeInfoList = zipcodeSet.stream().map(this::toZipcodeInfo).toList();

        FilterDto filterDto = new FilterDto();
        filterDto.setCityInfos(cityInfoList);
        filterDto.setCountyInfos(countyInfoList);
        filterDto.setVillageInfos(villageInfoList);
        filterDto.setZipcodeInfos(zipcodeInfoList);

        return filterDto;
    }

    default CityInfo toCityInfo(String city) {
        return new CityInfo(city);
    }

    default CountyInfo toCountyInfo(String county) {
        return new CountyInfo(county);
    }

    default VillageInfo toVillageInfo(String village) {
        return new VillageInfo(village);
    }

    default ZipcodeInfo toZipcodeInfo(String zipcode) {
        return new ZipcodeInfo(zipcode);
    }
}
