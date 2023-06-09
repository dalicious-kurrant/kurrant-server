package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterInfo;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;

import co.dalicious.integration.client.user.entity.Region;
import org.mapstruct.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestedMySpotZonesMapper {

    default FilterDto toFilterDto(List<FilterInfo> cityList, List<FilterInfo> countyList, List<FilterInfo> villageList, List<FilterInfo> zipcodeList) {

        FilterDto filterDto = new FilterDto();
        filterDto.setCityInfos(cityList);
        filterDto.setCountyInfos(countyList);
        filterDto.setVillageInfos(villageList);
        filterDto.setZipcodeInfos(zipcodeList);

        return filterDto;
    }

    default ListResponseDto toListResponseDto(RequestedMySpotZones requestedMySpotZones) {
        ListResponseDto listResponseDto = new ListResponseDto();

        listResponseDto.setId(requestedMySpotZones.getId());
        listResponseDto.setCity(requestedMySpotZones.getRegion().getCity());
        listResponseDto.setCounty(requestedMySpotZones.getRegion().getCounty());
        listResponseDto.setRequestUserCount(requestedMySpotZones.getWaitingUserCount());
        listResponseDto.setVillage(requestedMySpotZones.getRegion().getVillage());
        listResponseDto.setZipcode(requestedMySpotZones.getRegion().getZipcode());
        listResponseDto.setMemo(requestedMySpotZones.getMemo());

        return listResponseDto;
    }

    default RequestedMySpotZones toRequestedMySpotZones (Integer count, String memo, Region region, BigInteger userIds) {
        return RequestedMySpotZones.builder()
                .region(region)
                .waitingUserCount(count)
                .memo(memo)
                .userIds(userIds == null ? null : List.of(userIds))
                .build();
    }

}