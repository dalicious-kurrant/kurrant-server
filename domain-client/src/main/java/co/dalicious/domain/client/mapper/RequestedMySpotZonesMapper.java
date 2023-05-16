package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.mySpotZone.filter.*;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.RequestedMySpotZones;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import org.geolatte.geom.M;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public interface RequestedMySpotZonesMapper {

    default FilterDto toFilterDto(List<String> cityList, List<String> countyList, List<String> villageList, List<String> zipcodeList) {

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
        listResponseDto.setCity(requestedMySpotZones.getCity());
        listResponseDto.setCounty(requestedMySpotZones.getCounty());
        listResponseDto.setRequestUserCount(requestedMySpotZones.getWaitingUserCount());
        listResponseDto.setVillage(requestedMySpotZones.getVillage());
        listResponseDto.setZipcode(requestedMySpotZones.getZipcode());
        listResponseDto.setMemo(requestedMySpotZones.getMemo());

        return listResponseDto;
    }

    RequestedMySpotZones toRequestedMySpotZones (CreateRequestDto createRequestDto);

    default RequestedMySpotDetailDto toRequestedMySpotDetailDto(RequestedMySpotZones requestedMySpotZones) {
        RequestedMySpotDetailDto requestedMySpotDetailDto = new RequestedMySpotDetailDto();

        requestedMySpotDetailDto.setId(requestedMySpotDetailDto.getId());
        requestedMySpotDetailDto.setZipcode(requestedMySpotDetailDto.getZipcode());
        requestedMySpotDetailDto.setCity(requestedMySpotDetailDto.getCity());
        requestedMySpotDetailDto.setCounty(requestedMySpotDetailDto.getCounty());
        requestedMySpotDetailDto.setVillage(requestedMySpotDetailDto.getVillage());
        requestedMySpotDetailDto.setRequestUserCount(requestedMySpotDetailDto.getRequestUserCount());
        requestedMySpotDetailDto.setMemo(requestedMySpotDetailDto.getMemo());

        return requestedMySpotDetailDto;
    }

    default MySpotZone toMySpotZone(List<RequestedMySpotZones> requestedMySpotZonesList) {

        List<String> zipcodes = new ArrayList<>();
        List<String> counties = new ArrayList<>();
        List<String> villages = new ArrayList<>();
        Integer count = null;

        for(RequestedMySpotZones requestedMySpotZones : requestedMySpotZonesList) {
            if(zipcodes.isEmpty() || !zipcodes.contains(requestedMySpotZones.getZipcode())) zipcodes.add(requestedMySpotZones.getZipcode());
            if(counties.isEmpty() || !counties.contains(requestedMySpotZones.getZipcode())) counties.add(requestedMySpotZones.getCounty());
            if(villages.isEmpty() || !villages.contains(requestedMySpotZones.getZipcode())) villages.add(requestedMySpotZones.getVillage());

            if (count == null) count = requestedMySpotZones.getWaitingUserCount();

            count = count + requestedMySpotZones.getWaitingUserCount();
        }

        return MySpotZone.builder()
                .diningTypes(List.of(DiningType.MORNING, DiningType.LUNCH, DiningType.DINNER))
                .name(requestedMySpotZonesList.get(0).getCity() + "_(임시)")
                .mySpotZoneStatus(MySpotZoneStatus.WAITE)
                .zipcodes(zipcodes)
                .city(requestedMySpotZonesList.get(0).getCity())
                .countries(counties)
                .villages(villages)
                .mySpotZoneUserCount(count)
                .build();
    }
}
