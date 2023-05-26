package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterInfo;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.GenerateRandomNumber;
import org.mapstruct.Mapper;

import java.time.LocalTime;
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
        listResponseDto.setCity(requestedMySpotZones.getCity());
        listResponseDto.setCounty(requestedMySpotZones.getCounty());
        listResponseDto.setRequestUserCount(requestedMySpotZones.getWaitingUserCount());
        listResponseDto.setVillage(requestedMySpotZones.getVillage());
        listResponseDto.setZipcode(requestedMySpotZones.getZipcode());
        listResponseDto.setMemo(requestedMySpotZones.getMemo());

        return listResponseDto;
    }

    RequestedMySpotZones toRequestedMySpotZones (CreateRequestDto createRequestDto);

    default MySpotZone toMySpotZone(List<RequestedMySpotZones> requestedMySpotZonesList) {
        Integer count = null;

        for(RequestedMySpotZones requestedMySpotZones : requestedMySpotZonesList) {
            if (count == null) count = requestedMySpotZones.getWaitingUserCount();
            else count = count + requestedMySpotZones.getWaitingUserCount();
        }

        return MySpotZone.builder()
                .diningTypes(List.of(DiningType.MORNING, DiningType.LUNCH, DiningType.DINNER))
                .name("(임시)_" + GenerateRandomNumber.create6DigitKey())
                .mySpotZoneStatus(MySpotZoneStatus.WAITE)
                .mySpotZoneUserCount(count)
                .build();
    }

    default MySpotZoneMealInfo toMealInfo(Group group, DiningType diningType, LocalTime deliveryTime, String lastOrderTime, String useDays, String membershipBenefitTime) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || useDays == null) {
            return null;
        }

        return MySpotZoneMealInfo.builder()
                .group(group)
                .diningType(diningType)
                .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                .deliveryTimes(List.of(deliveryTime))
                .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                .build();

    }

    default Region toRegion (RequestedMySpotZones requestedMySpotZones, MySpotZone mySpotZone) {
        return Region.builder()
                .city(requestedMySpotZones.getCity())
                .country(requestedMySpotZones.getCounty())
                .village(requestedMySpotZones.getVillage())
                .zipcodes(requestedMySpotZones.getZipcode())
                .mySpotZone(mySpotZone)
                .build();
    }
}
