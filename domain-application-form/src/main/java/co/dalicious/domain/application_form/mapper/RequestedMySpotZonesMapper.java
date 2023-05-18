package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterInfo;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.DeliverySchedule;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.GenerateRandomNumber;
import org.mapstruct.Mapper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
                .name("(임시)_" + GenerateRandomNumber.create6DigitKey())
                .mySpotZoneStatus(MySpotZoneStatus.WAITE)
                .zipcodes(zipcodes)
                .city(requestedMySpotZonesList.get(0).getCity())
                .countries(counties)
                .villages(villages)
                .mySpotZoneUserCount(count)
                .build();
    }

    default MySpotZoneMealInfo toMealInfo(Group group, DiningType diningType, LocalTime deliveryTime, String lastOrderTime, String useDays, String membershipBenefitTime) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || useDays == null) {
            return null;
        }

        List<DeliverySchedule> deliveryScheduleList = Collections.singletonList(DeliverySchedule.builder().deliveryTime(deliveryTime).pickupTime(DateUtils.stringToLocalTime("00:00")).build());

        return MySpotZoneMealInfo.builder()
                .group(group)
                .diningType(diningType)
                .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                .deliveryScheduleList(deliveryScheduleList)
                .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                .build();

    }
}
