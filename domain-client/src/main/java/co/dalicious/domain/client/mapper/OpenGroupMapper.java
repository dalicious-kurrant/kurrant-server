package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.OpenGroupDetailDto;
import co.dalicious.domain.client.dto.OpenGroupListForKeywordDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.dto.OpenGroupSpotDetailDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {DateUtils.class, DiningType.class})
public interface OpenGroupMapper {

    @Mapping(source = "group.address", target = "address", qualifiedByName = "addressToString")
    @Mapping(target = "diningType", expression = "java(group.getDiningTypes().stream().map(DiningType::getCode).toList())")
    @Mapping(source = "group", target = "spotType", qualifiedByName = "setSpotType")
    @Mapping(source = "group.openGroupUserCount", target = "userCount")
    @Mapping(source = "distance", target = "distance")
    OpenGroupResponseDto toOpenGroupDto(OpenGroup group, Double distance) ;

    @AfterMapping
    default void toLocation(OpenGroup group, @MappingTarget OpenGroupResponseDto dto) {
        Map<String, String> location = group.getAddress().getLatitudeAndLongitude();
        dto.setLatitude(location.get("latitude"));
        dto.setLongitude(location.get("longitude"));
    }

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("setSpotType")
    default Integer setSpotType(Group group) {
        Integer code = null;
        if(Hibernate.unproxy(group) instanceof Corporation) code = GroupDataType.CORPORATION.getCode();
        if(Hibernate.unproxy(group) instanceof MySpotZone) code = GroupDataType.MY_SPOT.getCode();
        if(Hibernate.unproxy(group) instanceof OpenGroup) code = GroupDataType.OPEN_GROUP.getCode();

        return code;
    }

    default OpenGroupDetailDto toOpenGroupDetailDto(OpenGroup group) {
        OpenGroupDetailDto dto = new OpenGroupDetailDto();

        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setAddress(group.getAddress().addressToString());
        dto.setJibun(group.getAddress().stringToAddress3());
        dto.setUserCount(group.getOpenGroupUserCount());

        List<DiningType> diningTypes = group.getDiningTypes();

        dto.setDiningTypes(diningTypes.stream().map(DiningType::getCode).toList());

        diningTypes.forEach(diningType -> {

            MealInfo mealInfo = group.getMealInfo(diningType);
            switch (diningType) {
                case MORNING -> dto.setBreakfastDeliveryTime(mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
                case LUNCH -> dto.setLunchDeliveryTime(mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
                case DINNER -> dto.setDinnerDeliveryTime(mealInfo.getDeliveryTimes().stream().map(DateUtils::timeToString).toList());
            }
        });

        dto.setSpotDetailDtos(toOpenGroupSpotDetailDto(group.getSpots()));

        return dto;
    };
    default List<OpenGroupSpotDetailDto> toOpenGroupSpotDetailDto (List<Spot> spotList) {
        List<OpenGroupSpotDetailDto> openGroupSpotDetailDtoList = new ArrayList<>();

        spotList.forEach(spot ->  {
            OpenGroupSpotDetailDto openGroupSpotDetailDto = new OpenGroupSpotDetailDto();

            openGroupSpotDetailDto.setName(spot.getName());
            openGroupSpotDetailDto.setIsRestriction(((OpenGroupSpot) spot).getIsRestriction());

            openGroupSpotDetailDtoList.add(openGroupSpotDetailDto);
        });

        return openGroupSpotDetailDtoList;
    }

    @Mapping(source = "address.address3", target = "jibunAddress")
    @Mapping(source = "group", target = "address", qualifiedByName = "mappingAddress")
    OpenGroupListForKeywordDto toOpenGroupListForKeywordDto(Group group);

    @Named("mappingAddress")
    default String mappingAddress(Group group) {
        return group.getAddress().addressToString() + " " + group.getName();
    }

    @AfterMapping
    default void afterMappingLocation(Group group, @MappingTarget OpenGroupListForKeywordDto dto) {
        Map<String, String> location = group.getAddress().getLatitudeAndLongitude();
        dto.setLatitude(location.get("latitude"));
        dto.setLongitude(location.get("longitude"));
    }
}
