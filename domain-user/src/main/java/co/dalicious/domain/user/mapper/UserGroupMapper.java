package co.dalicious.domain.user.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.GroupDetailDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.dto.corporation.CorporationResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.user.dto.UserGroupDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.client.entity.MySpot;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningTypesUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {

    default SpotListResponseDto toSpotListResponseDto(UserGroup userGroup) {
        SpotListResponseDto spotListResponseDto = new SpotListResponseDto();

        Group group = (Group) Hibernate.unproxy(userGroup.getGroup());
        if (group instanceof MySpotZone mySpotZone) {
            spotListResponseDto.setClientId(mySpotZone.getId());
            spotListResponseDto.setSpots(getSpots(group, userGroup.getUser()));
        } else {
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

        if (group instanceof MySpotZone) {
            spotDtoList = group.getSpots().stream()
                    .filter(mySpot -> mySpot.getStatus().equals(SpotStatus.ACTIVE) && ((MySpot) mySpot).getUserId().equals(user.getId()) && !((MySpot) mySpot).getIsDelete())
                    .map(this::toSpot).toList();
        } else {
            spotDtoList = group.getSpots().stream().filter(spot -> spot.getStatus().equals(SpotStatus.ACTIVE))
                    .map(spot -> {
                        SpotListResponseDto.Spot s = toSpot(spot);
                        if (spot instanceof OpenGroupSpot openGroupSpot)
                            s.setIsRestriction(openGroupSpot.getIsRestriction());

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

    default UserGroup toUserGroup(User user, Group group, ClientStatus clientStatus) {
        return UserGroup.builder()
                .clientStatus(clientStatus)
                .user(user)
                .group(group)
                .build();
    }

    CorporationResponseDto toCorporationResponseDto(Group group);

    default List<CorporationResponseDto> toCorporationResponseDtoList(List<UserGroup> userGroups) {
        return userGroups.stream()
                .map(v -> {
                    Group group = v.getGroup();
                    CorporationResponseDto dto = toCorporationResponseDto(group);

                    Map<String, String> location = group.getAddress().getLatitudeAndLongitude();
                    dto.setLatitude(location.get("latitude"));
                    dto.setLongitude(location.get("longitude"));

                    return dto;
                }).toList();
    }

    default UserGroupDto toUserGroupDto(User user) {
        UserGroupDto userGroupDto = new UserGroupDto();
        List<UserGroup> userGroups = user.getActiveUserGroups();
        Integer mySpotCount = 0;
        Integer shareSpotCount = 0;
        Integer privateSpotCount = 0;
        String defaultSpotName = null;
        List<UserGroupDto.GroupInfo> groups = new ArrayList<>();

        for (UserGroup userGroup : userGroups) {
            if (Hibernate.unproxy(userGroup.getGroup()) instanceof MySpotZone mySpotZone) {
                mySpotCount++;
                groups.add(MySpotZonetoGroupInfo(mySpotZone, user));
            }
            if (Hibernate.getClass(userGroup.getGroup()).equals(OpenGroup.class)) {
                shareSpotCount++;
                groups.add(toGroupInfo(userGroup.getGroup()));
            }
            if (Hibernate.getClass(userGroup.getGroup()).equals(Corporation.class)) {
                privateSpotCount++;
                groups.add(toGroupInfo(userGroup.getGroup()));
            }
        }
        UserSpot userSpot = user.getDefaultUserSpot();
        if(userSpot != null && !Hibernate.getClass(userSpot.getSpot()).equals(MySpot.class)) {
            defaultSpotName = userSpot.getSpot().getGroup().getName() + userSpot.getSpot().getName();
        }
        if(userSpot != null && Hibernate.getClass(userSpot.getSpot()).equals(MySpot.class)) {
            defaultSpotName = userSpot.getSpot().getName() == null ?
                    userSpot.getSpot().getAddress().addressToString() : userSpot.getSpot().getName();
        }

        userGroupDto.setMySpotCount(mySpotCount);
        userGroupDto.setShareSpotCount(shareSpotCount);
        userGroupDto.setPrivateSpotCount(privateSpotCount);
        userGroupDto.setDefaultSpotName(defaultSpotName);
        userGroupDto.setGroups(groups);
        return userGroupDto;
    }

    default UserGroupDto.GroupInfo toGroupInfo(Group group) {
        UserGroupDto.GroupInfo groupInfo = new UserGroupDto.GroupInfo();
        groupInfo.setGroupId(group.getId());
        groupInfo.setGroupType(getGroupType(group));
        groupInfo.setGroupName(group.getName());
        return groupInfo;
    }

    default UserGroupDto.GroupInfo MySpotZonetoGroupInfo(MySpotZone mySpotZone, User user) {
        UserGroupDto.GroupInfo groupInfo = new UserGroupDto.GroupInfo();
        groupInfo.setGroupId(mySpotZone.getId());
        groupInfo.setGroupType(getGroupType(mySpotZone));
        groupInfo.setGroupName(mySpotZone.getMySpot(user.getId()).getName());
        return groupInfo;
    }

    default String getGroupType(Group group) {
        if (Hibernate.getClass(group).equals(Corporation.class)) return "프라이빗스팟";
        if (Hibernate.getClass(group).equals(OpenGroup.class)) return "공유스팟";
        if (Hibernate.getClass(group).equals(MySpotZone.class)) return "마이스팟";
        return null;
    }

    default GroupDetailDto toGroupDetailDto(Group group, User user) {
        if(Hibernate.getClass(group).equals(Corporation.class)) {
            return GroupDetailDto.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .address(toAddressString(group.getAddress()))
                    .diningTypes(DiningTypesUtils.diningTypesToCodes(group.getDiningTypes()))
                    .mealInfos(toMealInfoDtos(group.getMealInfos()))
                    .spots(toSpotDtos(group.getSpots()))
                    .build();
        }
        if(Hibernate.unproxy(group) instanceof OpenGroup openGroup) {
            return GroupDetailDto.builder()
                    .id(openGroup.getId())
                    .name(openGroup.getName())
                    .address(toAddressString(openGroup.getAddress()))
                    .userCount(openGroup.getOpenGroupUserCount())
                    .diningTypes(DiningTypesUtils.diningTypesToCodes(openGroup.getDiningTypes()))
                    .mealInfos(toMealInfoDtos(openGroup.getMealInfos()))
                    .spots(toSpotDtos(openGroup.getSpots()))
                    .build();
        }
        if(Hibernate.unproxy(group) instanceof MySpotZone mySpotZone) {
            return GroupDetailDto.builder()
                    .id(mySpotZone.getId())
                    .name(mySpotZone.getMySpot(user.getId()).getName())
                    .address(toAddressString(mySpotZone.getMySpot(user.getId()).getAddress()))
                    .phone(mySpotZone.getMySpot(user.getId()).getPhone())
                    .diningTypes(DiningTypesUtils.diningTypesToCodes(mySpotZone.getDiningTypes()))
                    .mealInfos(toMealInfoDtos(mySpotZone.getMealInfos()))
                    .build();
        }
        return null;
    }

    default String toAddressString(Address address) {
        return address.addressToString() + " (" + address.stringToAddress3() + ")";
    }

    default GroupDetailDto.MealInfo toMealInfoDto(MealInfo mealInfo) {
        return GroupDetailDto.MealInfo.builder()
                .diningType(mealInfo.getDiningType().getCode())
                .lastOrderTime(DayAndTime.dayAndTimeToString(mealInfo.getLastOrderTime()))
                .membershipBenefitTime(DayAndTime.dayAndTimeToString(mealInfo.getMembershipBenefitTime()))
                .deliveryTimes(DateUtils.timesToStringList(mealInfo.getDeliveryTimes()))
                .build();
    }

    default List<GroupDetailDto.MealInfo> toMealInfoDtos(List<MealInfo> mealInfos) {
        return mealInfos.stream()
                .map(this::toMealInfoDto)
                .toList();
    }

    default GroupDetailDto.SpotInfo toSpotDto(Spot spot) {
        if(Hibernate.unproxy(spot) instanceof OpenGroupSpot openGroupSpot) {
            return GroupDetailDto.SpotInfo.builder()
                    .spotId(openGroupSpot.getId())
                    .spotName(openGroupSpot.getName())
                    .isRestriction(openGroupSpot.getIsRestriction())
                    .build();
        }
        if(Hibernate.unproxy(spot) instanceof CorporationSpot corporationSpot) {
            return GroupDetailDto.SpotInfo.builder()
                    .spotId(corporationSpot.getId())
                    .spotName(corporationSpot.getName())
                    .build();
        }
        return null;
    }

    default List<GroupDetailDto.SpotInfo> toSpotDtos(List<Spot> spots) {
        return spots.stream()
                .map(this::toSpotDto)
                .toList();
    }

}
