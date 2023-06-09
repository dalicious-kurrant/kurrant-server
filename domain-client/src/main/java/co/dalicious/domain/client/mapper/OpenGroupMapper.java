package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.OpenGroupDetailDto;
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

@Mapper(componentModel = "spring", imports = {DateUtils.class, DiningType.class})
public interface OpenGroupMapper {

    @Mapping(source = "group.address", target = "address", qualifiedByName = "addressToString")
    @Mapping(target = "diningType", expression = "java(group.getDiningTypes().stream().map(DiningType::getCode).toList())")
    @Mapping(source = "group", target = "spotType", qualifiedByName = "setSpotType")
    @Mapping(source = "group.openGroupUserCount", target = "userCount")
    OpenGroupResponseDto toOpenGroupDto(OpenGroup group, Double distance) ;

    @AfterMapping
    default void toLocation(OpenGroup group, @MappingTarget OpenGroupResponseDto dto) {
        String location = group.getAddress().locationToString();

        dto.setLatitude(location.split(" ")[0]);
        dto.setLongitude(location.split(" ")[1]);
    }

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("setSpotType")
    default Integer setSpotType(Group group) {
        Integer code = null;
        if(Hibernate.unproxy(group) instanceof Corporation) code = GroupDataType.CORPORATION.getCode();
        if(Hibernate.unproxy(group) instanceof Apartment) code = GroupDataType.MY_SPOT.getCode();
        if(Hibernate.unproxy(group) instanceof OpenGroup) code = GroupDataType.OPEN_GROUP.getCode();

        return code;
    }

    default OpenGroupDetailDto toOpenGroupDetailDto(OpenGroup group) {
        OpenGroupDetailDto dto = new OpenGroupDetailDto();

        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setAddress(group.getAddress().addressToString());
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
}