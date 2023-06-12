package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.domain.client.entity.MySpotZone;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {

    default SpotListResponseDto toSpotListResponseDto(Group group, List<MySpot> mySpotList) {
        SpotListResponseDto spotListResponseDto = new SpotListResponseDto();

        if(Hibernate.unproxy(group) instanceof MySpotZone mySpotZone) {
            spotListResponseDto.setClientId(mySpotZone.getId());
            spotListResponseDto.setSpots(getSpots(mySpotZone, mySpotList));
        }
        else {
            spotListResponseDto.setClientId(group.getId());
            spotListResponseDto.setClientName(group.getName());
            spotListResponseDto.setSpots(getSpots(group, mySpotList));
        }

        if(Hibernate.unproxy(group) instanceof Corporation) spotListResponseDto.setSpotType(GroupDataType.CORPORATION.getCode());
        else if(Hibernate.unproxy(group) instanceof MySpotZone) spotListResponseDto.setSpotType(GroupDataType.MY_SPOT.getCode());
        else if(Hibernate.unproxy(group) instanceof OpenGroup) spotListResponseDto.setSpotType(GroupDataType.OPEN_GROUP.getCode());

        return spotListResponseDto;
    };

    default List<SpotListResponseDto.Spot> getSpots(Group group, List<MySpot> mySpotList) {
        List<SpotListResponseDto.Spot> spotDtoList;

        if(group instanceof MySpotZone) {
            spotDtoList = mySpotList.stream().filter(mySpot -> mySpot.getStatus().equals(SpotStatus.ACTIVE))
                    .map(mySpot -> SpotListResponseDto.Spot.builder()
                            .spotId(mySpot.getId())
                            .spotName(mySpot.getName() == null ? mySpot.getAddress().addressToString() : mySpot.getName())
                            .build()).toList();
        }
        else {
            spotDtoList = group.getSpots().stream().filter(spot -> spot.getStatus().equals(SpotStatus.ACTIVE))
                    .map(spot -> SpotListResponseDto.Spot.builder()
                            .spotName(spot.getName())
                            .spotId(spot.getId())
                            .build()).toList();
        }

        return spotDtoList;
    }

    default GroupCountDto toGroupCountDto(List<SpotListResponseDto> spotListResponseDtoList) {

        AtomicInteger privateCount = new AtomicInteger();
        AtomicInteger shareSpotCount = new AtomicInteger();
        AtomicInteger mySpotCount = new AtomicInteger();

        spotListResponseDtoList.forEach(spotListResponseDto ->  {
            if(spotListResponseDto.getSpotType().equals(GroupDataType.CORPORATION.getCode())) privateCount.getAndIncrement();
            else if (spotListResponseDto.getSpotType().equals(GroupDataType.OPEN_GROUP.getCode())) shareSpotCount.getAndIncrement();
            else if (spotListResponseDto.getSpotType().equals(GroupDataType.MY_SPOT.getCode())) mySpotCount.set(spotListResponseDto.getSpots().size());
        });

        GroupCountDto groupCountDto = new GroupCountDto();

        groupCountDto.setPrivateCount(privateCount.get());
        groupCountDto.setMySpotCount(mySpotCount.get());
        groupCountDto.setShareSpotCount(shareSpotCount.get());
        groupCountDto.setSpotListResponseDtoList(spotListResponseDtoList);

        return groupCountDto;
    }

    default UserGroup toUserGroup(User user, Group group) {
        return UserGroup.builder()
                .clientStatus(ClientStatus.BELONG)
                .user(user)
                .group(group)
                .build();
    }
}
