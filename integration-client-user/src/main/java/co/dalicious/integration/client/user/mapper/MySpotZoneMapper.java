package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.client.entity.*;
import co.dalicious.integration.client.user.entity.MySpotZone;
import co.dalicious.integration.client.user.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.integration.client.user.dto.filter.FilterDto;
import co.dalicious.integration.client.user.dto.filter.FilterInfo;
import co.dalicious.integration.client.user.dto.filter.FilterStatusDto;
import co.dalicious.integration.client.user.dto.mySpotZone.AdminListResponseDto;
import co.dalicious.integration.client.user.dto.mySpotZone.CreateRequestDto;
import co.dalicious.domain.address.entity.Region;
import co.dalicious.system.util.GenerateRandomNumber;
import org.mapstruct.Mapper;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MySpotZoneMapper {

    default FilterDto toFilterDto(List<MySpotZone> mySpotZoneList, Map<BigInteger, String> cityMap, Map<BigInteger, String> countyMap, Map<BigInteger, String> villageMap, Map<BigInteger, String> zipcodeMap, List<MySpotZoneStatus> statusList) {

        List<FilterInfo> nameList = mySpotZoneList.stream().map(mySpotZone -> toFilterInfo(mySpotZone.getId(), mySpotZone.getName())).toList();
        List<FilterInfo> cityList = cityMap.keySet().stream().map(v -> toFilterInfo(v, cityMap.get(v))).toList();
        List<FilterInfo> countyList = countyMap.keySet().stream().map(v -> toFilterInfo(v, countyMap.get(v))).toList();
        List<FilterInfo> villageList = villageMap.keySet().stream().map(v -> toFilterInfo(v, villageMap.get(v))).toList();
        List<FilterInfo> zipcodeList = zipcodeMap.keySet().stream().map(v -> toFilterInfo(v, zipcodeMap.get(v))).toList();

        return new FilterDto(nameList, cityList, countyList, villageList, zipcodeList, statusList.stream().map(this::toFilterStatusDto).toList());
    }
    FilterInfo toFilterInfo(BigInteger id, String name);
    FilterStatusDto toFilterStatusDto(MySpotZoneStatus mySpotZoneStatus);

    default AdminListResponseDto toAdminListResponseDto(MySpotZone mySpotZone, List<Region> regionList) {

        List<Region> mySpotZoneRegion = regionList.stream().filter(region -> mySpotZone.getRegionIds().contains(region.getId())).findFirst().stream().toList();

        AdminListResponseDto adminListResponseDto = new AdminListResponseDto();

        adminListResponseDto.setId(mySpotZone.getId());
        adminListResponseDto.setName(mySpotZone.getName());
        adminListResponseDto.setStatus(mySpotZone.getMySpotZoneStatus().getCode());
        adminListResponseDto.setUserCount(mySpotZone.getMySpotZoneUserCount());
        adminListResponseDto.setOpenStartDate(DateUtils.format(mySpotZone.getOpenStartDate()));
        adminListResponseDto.setOpenCloseDate(DateUtils.format(mySpotZone.getOpenCloseDate()));

        Set<String> counties = new HashSet<>();
        Set<String> villages = new HashSet<>();
        Set<String> zipcodes = new HashSet<>();

        for(Region region : mySpotZoneRegion) {
            counties.add(region.getCounty());
            villages.add(region.getVillage());
            zipcodes.add(region.getZipcode());
        }

        adminListResponseDto.setCity(regionList.isEmpty() ? null : regionList.get(0).getCity());
        adminListResponseDto.setCounties(counties);
        adminListResponseDto.setVillages(villages);
        adminListResponseDto.setZipcodes(zipcodes);

        List<DiningType> diningTypeList = mySpotZone.getDiningTypes();

        diningTypeList.forEach(diningType -> {
            MealInfo mealInfo = mySpotZone.getMealInfo(diningType);

            switch (diningType) {
                case MORNING -> adminListResponseDto.setBreakfastDeliveryTime(mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
                case LUNCH -> adminListResponseDto.setLunchDeliveryTime(mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
                case DINNER -> adminListResponseDto.setDinnerDeliveryTime(mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
            }
        });

        adminListResponseDto.setDiningType(diningTypeList.stream().map(DiningType::getCode).toList());

        return adminListResponseDto;
    }

    default MySpotZone toMySpotZone(CreateRequestDto createRequestDto, List<BigInteger> regionIds) {
        return MySpotZone.builder()
                .name(createRequestDto.getName())
                .diningTypes(createRequestDto.getDiningTypes().stream().map(DiningType::ofCode).toList())
                .mySpotZoneStatus(MySpotZoneStatus.ofCode(createRequestDto.getStatus()))
                .mySpotZoneUserCount(createRequestDto.getUserCount())
                .openStartDate(DateUtils.stringToDate(createRequestDto.getOpenStartDate()))
                .openCloseDate(DateUtils.stringToDate(createRequestDto.getOpenCloseDate()))
                .regionIds(regionIds)
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

}
