package co.kurrant.app.public_api.mapper.user;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.kurrant.app.public_api.dto.user.UserHomeResponseDto;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring")
@Transactional
public interface UserHomeInfoMapper {
    @Mapping(source = "userSpots", target = "spotType", qualifiedByName = "getSpotTypeCode")
    @Mapping(source = "userSpots", target = "spotId", qualifiedByName = "getSpotId")
    @Mapping(source = "userSpots", target = "spot", qualifiedByName = "getSpotName")
    @Mapping(source = "userSpots", target = "groupId", qualifiedByName = "getGroupId")
    @Mapping(source = "userSpots", target = "group", qualifiedByName = "getGroupName")
    @Mapping(source = "id", target = "userId")
    UserHomeResponseDto toDto(User user);

    @Named("getSpotTypeCode")
    default Integer getSpotTypeCode(List<UserSpot> userSpots) {
        return userSpots.stream()
                .filter(UserSpot::getIsDefault)
                .map(spot -> spot.getClientType().getCode())
                .map(code -> {
                    if (code == 0) return GroupDataType.APARTMENT.getCode();
                    else if (code == 1) return GroupDataType.CORPORATION.getCode();
                    else if (code == 2) return GroupDataType.OPEN_GROUP.getCode();
                    return null;
                })
                .findAny()
                .orElse(null);
    }

    @Named("getSpotId")
    default BigInteger getSpotId(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        Optional<UserSpot> userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny();
        return userSpot.map(spot -> spot.getSpot().getId()).orElse(null);
    }

    @Named("getSpotName")
    default String getSpotName(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        Optional<UserSpot> userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny();
        return userSpot.map(spot -> spot.getSpot().getName()).orElse(null);
    }

    @Named("getGroupId")
    default BigInteger getGroupId(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        Optional<UserSpot> userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny();
        return userSpot.map(spot -> spot.getSpot().getGroup().getId()).orElse(null);
    }

    @Named("getGroupName")
    default String getGroupName(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        Optional<UserSpot> userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny();
        return userSpot.map(spot -> spot.getSpot().getGroup().getName()).orElse(null);
    }

}
