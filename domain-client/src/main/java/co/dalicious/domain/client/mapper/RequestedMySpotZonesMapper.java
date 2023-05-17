package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.mySpotZone.filter.*;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.DeliverySchedule;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.GenerateRandomNumber;
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
                .name("(임시)_" + GenerateRandomNumber.create6DigitKey())
                .mySpotZoneStatus(MySpotZoneStatus.WAITE)
                .zipcodes(zipcodes)
                .city(requestedMySpotZonesList.get(0).getCity())
                .countries(counties)
                .villages(villages)
                .mySpotZoneUserCount(count)
                .build();
    }

    default MealInfo toMealInfo(Group group, DiningType diningType, String lastOrderTime, String useDays, String membershipBenefitTime) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || useDays == null) {
            return null;
        }

        DeliverySchedule deliverySchedule = DeliverySchedule.builder().deliveryTime(deliveryTime).pickupTime("00:00").build();

        return MySpotZoneMealInfo.builder()
                .group(group)
                .diningType(diningType)
                .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                .deliveryScheduleList()
                .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                .build();

    }
}
