package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.application_form.dto.mySpotZone.UpdateRequestDto;
import co.dalicious.domain.application_form.dto.mySpotZone.UpdateStatusDto;
import co.dalicious.integration.client.user.entity.Region;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.domain.client.dto.filter.FilterDto;
import co.dalicious.domain.client.dto.FilterInfo;
import co.dalicious.domain.client.dto.filter.FilterStatusDto;
import co.dalicious.domain.application_form.dto.mySpotZone.AdminListResponseDto;
import co.dalicious.domain.application_form.dto.mySpotZone.CreateRequestDto;
import co.dalicious.system.util.GenerateRandomNumber;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MySpotZoneMapper {

    default FilterDto toFilterDto(List<FilterInfo> nameList, Map<BigInteger, String> cityMap, Map<BigInteger, String> countyMap, Map<BigInteger, String> villageMap, Map<BigInteger, String> zipcodeMap, List<MySpotZoneStatus> statusList) {

        List<FilterInfo> cityList = cityMap.keySet().stream().map(v -> toFilterInfo(v, cityMap.get(v))).toList();
        List<FilterInfo> countyList = countyMap.keySet().stream().map(v -> toFilterInfo(v, countyMap.get(v))).toList();
        List<FilterInfo> villageList = villageMap.keySet().stream().map(v -> toFilterInfo(v, villageMap.get(v))).toList();
        List<FilterInfo> zipcodeList = zipcodeMap.keySet().stream().map(v -> toFilterInfo(v, zipcodeMap.get(v))).toList();

        return new FilterDto(nameList, cityList, countyList, villageList, zipcodeList, statusList.stream().map(this::toFilterStatusDto).toList());
    }
    FilterInfo toFilterInfo(BigInteger id, String name);
    FilterStatusDto toFilterStatusDto(MySpotZoneStatus mySpotZoneStatus);

    default AdminListResponseDto toAdminListResponseDto(MySpotZone mySpotZone, List<Region> regionList) {

        List<Region> mySpotZoneRegion = regionList.stream().filter(region -> mySpotZone.getId().equals(region.getMySpotZoneIds())).findFirst().stream().toList();

        AdminListResponseDto adminListResponseDto = new AdminListResponseDto();

        adminListResponseDto.setId(mySpotZone.getId());
        adminListResponseDto.setName(mySpotZone.getName());
        adminListResponseDto.setStatus(mySpotZone.getMySpotZoneStatus().getCode());
        adminListResponseDto.setUserCount(mySpotZone.getMySpotZoneUserCount());
        adminListResponseDto.setOpenDate(DateUtils.format(mySpotZone.getOpenDate()));
        adminListResponseDto.setCloseDate(DateUtils.format(mySpotZone.getCloseDate()));

        Set<String> counties = new HashSet<>();
        Set<String> villages = new HashSet<>();
        Set<String> zipcodes = new HashSet<>();

        for(Region region : mySpotZoneRegion) {
            counties.add(region.getCounty());
            villages.add(region.getVillage());
            zipcodes.add(region.getZipcode());
        }

        adminListResponseDto.setCity(mySpotZoneRegion.isEmpty() ? null : mySpotZoneRegion.get(0).getCity());
        adminListResponseDto.setCounties(counties);
        adminListResponseDto.setVillages(villages);
        adminListResponseDto.setZipcodes(zipcodes);

        List<DiningType> diningTypeList = mySpotZone.getDiningTypes();

        diningTypeList.forEach(diningType -> {
            MealInfo mealInfo = mySpotZone.getMealInfo(diningType);

            switch (diningType) {
                case MORNING -> adminListResponseDto.setBreakfastDeliveryTime(mealInfo == null ? null : mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
                case LUNCH -> adminListResponseDto.setLunchDeliveryTime(mealInfo == null ? null : mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
                case DINNER -> adminListResponseDto.setDinnerDeliveryTime(mealInfo == null ? null : mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
            }
        });

        adminListResponseDto.setDiningType(diningTypeList.stream().map(DiningType::getCode).toList());

        return adminListResponseDto;
    }

    default MySpotZone toMySpotZone(CreateRequestDto createRequestDto) {
        return MySpotZone.builder()
                .name(createRequestDto.getName())
                .diningTypes(createRequestDto.getDiningTypes().stream().map(DiningType::ofCode).toList())
                .mySpotZoneStatus(MySpotZoneStatus.ofCode(createRequestDto.getStatus()))
                .mySpotZoneUserCount(createRequestDto.getUserCount())
                .openDate(DateUtils.stringToDate(createRequestDto.getOpenDate()))
                .closeDate(DateUtils.stringToDate(createRequestDto.getCloseDate()))
                .memo(createRequestDto.getMemo())
                .build();
    }

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

    default void updateMySpotZone(UpdateRequestDto updateRequestDto, @MappingTarget MySpotZone mySpotZone) {
        List<DiningType> diningTypes = updateRequestDto.getDiningTypes().stream().map(DiningType::ofCode).toList();
        mySpotZone.updateGroup(diningTypes, updateRequestDto.getName(), updateRequestDto.getMemo());
        mySpotZone.setMySpotZoneStatus(MySpotZoneStatus.ofCode(updateRequestDto.getStatus()));
        mySpotZone.setOpenDate(DateUtils.stringToDate(updateRequestDto.getOpenDate()));
        mySpotZone.setCloseDate(DateUtils.stringToDate(updateRequestDto.getCloseDate()));
    }

    default void updateMySpotZoneStatusAndDate(UpdateStatusDto updateStatusDto, @MappingTarget MySpotZone entity){
        entity.setMySpotZoneStatus(MySpotZoneStatus.ofCode(updateStatusDto.getStatus()));

        if (MySpotZoneStatus.ofCode(updateStatusDto.getStatus()).equals(MySpotZoneStatus.OPEN)) entity.setOpenDate(updateStatusDto.getStartDate());
        else if (MySpotZoneStatus.ofCode(updateStatusDto.getStatus()).equals(MySpotZoneStatus.CLOSE)) entity.setCloseDate(updateStatusDto.getStartDate());
    };

}
