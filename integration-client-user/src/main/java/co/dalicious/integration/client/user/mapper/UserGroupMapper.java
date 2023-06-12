package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.integration.client.user.entity.MySpot;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {

    default SpotListResponseDto toSpotListResponseDto(UserGroup userGroup) {
        SpotListResponseDto spotListResponseDto = new SpotListResponseDto();

        Group group = (Group) Hibernate.unproxy(userGroup.getGroup());
        if(group instanceof MySpotZone mySpotZone) {
            spotListResponseDto.setClientId(mySpotZone.getId());
            spotListResponseDto.setSpots(getSpots(group, userGroup.getUser()));
        }
        else {
            spotListResponseDto.setClientId(group.getId());
            spotListResponseDto.setClientName(group.getName());
            spotListResponseDto.setSpots(getSpots(group, userGroup.getUser()));
        }

        if (group instanceof Corporation)
            spotListResponseDto.setSpotType(GroupDataType.CORPORATION.getCode());
        else if (group instanceof MySpotZone)
            spotListResponseDto.setSpotType(GroupDataType.MY_SPOT.getCode());
        else if (group instanceof OpenGroup)
            spotListResponseDto.setSpotType(GroupDataType.OPEN_GROUP.getCode());

        return spotListResponseDto;
    }

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    SpotListResponseDto.Spot toSpot(Spot spot);

    default List<SpotListResponseDto.Spot> getSpots(Group group, User user) {
        List<SpotListResponseDto.Spot> spotDtoList;

        if(group instanceof MySpotZone) {
            List<Spot> spot = group.getSpots();
            spotDtoList = group.getSpots().stream()
                    .filter(mySpot -> mySpot.getStatus().equals(SpotStatus.ACTIVE) && ((MySpot) mySpot).getUserId().equals(user.getId()) && !((MySpot) mySpot).getIsDelete())
                    .map(this::toSpot).toList();
        }
        else {
            spotDtoList = group.getSpots().stream().filter(spot -> spot.getStatus().equals(SpotStatus.ACTIVE))
                    .map(spot -> {
                        SpotListResponseDto.Spot s = toSpot(spot);
                        if(spot instanceof OpenGroupSpot openGroupSpot) s.setIsRestriction(openGroupSpot.getIsRestriction());

                        return s;
                    }).toList();
        }
        return spotDtoList;
    }

    default GroupCountDto toGroupCountDto(List<SpotListResponseDto> spotListResponseDtoList) {

        AtomicInteger privateCount = new AtomicInteger();
        AtomicInteger shareSpotCount = new AtomicInteger();
        AtomicInteger mySpotCount = new AtomicInteger();

        spotListResponseDtoList.forEach(spotListResponseDto -> {
            if (spotListResponseDto.getSpotType().equals(GroupDataType.CORPORATION.getCode()))
                privateCount.getAndIncrement();
            else if (spotListResponseDto.getSpotType().equals(GroupDataType.OPEN_GROUP.getCode()))
                shareSpotCount.getAndIncrement();
            else if (spotListResponseDto.getSpotType().equals(GroupDataType.MY_SPOT.getCode()))
                mySpotCount.getAndIncrement();
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
