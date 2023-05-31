package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public interface GroupResponseMapper {
    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "spots", target = "spots", qualifiedByName = "spotToDto")
    @Mapping(source = "corporation", target = "spotType", qualifiedByName = "setSpotType")
    SpotListResponseDto toDto(Group corporation);

    @Named("spotToDto")
    default List<SpotListResponseDto.Spot> spotToDto(List<Spot> spots) {
        List<SpotListResponseDto.Spot> spotDtoList = new ArrayList<>();
        for (Spot spot : spots) {
            if(spot.getStatus().equals(SpotStatus.ACTIVE)) {
                spotDtoList.add(SpotListResponseDto.Spot.builder()
                        .spotName(spot.getName())
                        .spotId(spot.getId())
                        .build());
            }
        }
        return spotDtoList;
    }

    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "group", target = "spotType", qualifiedByName = "setSpotType")
    OpenGroupResponseDto toOpenGroupDto(Group group);

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

    default GroupCountDto toGroupCountDto(List<SpotListResponseDto> spotListResponseDtoList) {

        AtomicInteger privateCount = new AtomicInteger();
        AtomicInteger mySpotCount = new AtomicInteger();
        AtomicInteger shareSpotCount = new AtomicInteger();

        spotListResponseDtoList.forEach(spotListResponseDto ->  {
            if(spotListResponseDto.getSpotType().equals(GroupDataType.CORPORATION.getCode())) privateCount.getAndIncrement();
            else if (spotListResponseDto.getSpotType().equals(GroupDataType.OPEN_GROUP.getCode())) shareSpotCount.getAndIncrement();
            else if (spotListResponseDto.getSpotType().equals(GroupDataType.MY_SPOT.getCode())) mySpotCount.getAndIncrement();
        });

        GroupCountDto groupCountDto = new GroupCountDto();

        groupCountDto.setPrivateCount(privateCount.get());
        groupCountDto.setMySpotCount(mySpotCount.get());
        groupCountDto.setShareSpotCount(shareSpotCount.get());
        groupCountDto.setSpotListResponseDtoList(spotListResponseDtoList);

        return groupCountDto;
    }
}
