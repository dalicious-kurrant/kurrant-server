package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.filter.FilterDto;
import co.dalicious.domain.client.dto.filter.FilterInfo;
import co.dalicious.domain.client.dto.filter.FilterStatusDto;
import co.dalicious.domain.client.dto.mySpotZone.AdminListResponseDto;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.Region;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MySpotZoneMapper {

    default FilterDto toFilterDto(List<FilterInfo> nameList, List<FilterInfo> cityList, List<FilterInfo> countyList, List<FilterInfo> villageList, List<FilterInfo> zipcodeList, List<MySpotZoneStatus> statusList) {
        return new FilterDto(nameList, cityList, countyList, villageList, zipcodeList, statusList.stream().map(this::toFilterStatusDto).toList());
    }

    FilterStatusDto toFilterStatusDto(MySpotZoneStatus mySpotZoneStatus);

    default AdminListResponseDto toAdminListResponseDto(MySpotZone mySpotZone) {
        AdminListResponseDto adminListResponseDto = new AdminListResponseDto();

        adminListResponseDto.setId(mySpotZone.getId());
        adminListResponseDto.setName(mySpotZone.getName());
        adminListResponseDto.setStatus(mySpotZone.getMySpotZoneStatus().getCode());
        adminListResponseDto.setUserCount(mySpotZone.getMySpotZoneUserCount());
        adminListResponseDto.setOpenStartDate(DateUtils.format(mySpotZone.getOpenStartDate()));
        adminListResponseDto.setOpenCloseDate(DateUtils.format(mySpotZone.getOpenCloseDate()));

        List<Region> regionList = mySpotZone.getRegionList();

        Set<String> counties = new HashSet<>();
        Set<String> villages = new HashSet<>();
        List<String> zipcodes = new ArrayList<>();

        for(Region region : regionList) {
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
}
