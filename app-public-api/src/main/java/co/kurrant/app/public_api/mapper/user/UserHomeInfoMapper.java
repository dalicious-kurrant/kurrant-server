package co.kurrant.app.public_api.mapper.user;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.client.entity.MySpot;
import co.kurrant.app.public_api.dto.user.UserHomeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;


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
                .map(spot -> spot.getGroupDataType().getCode())
                .findAny()
                .orElse(null);
    }

    @Named("getSpotId")
    default BigInteger getSpotId(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        UserSpot userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny().orElse(null);
        return userSpot == null ? null : userSpot.getSpot().getId();
    }

    @Named("getSpotName")
    default String getSpotName(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        UserSpot userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny().orElse(null);
        return userSpot == null ? null : userSpot.getSpot().getName();
    }

    @Named("getGroupId")
    default BigInteger getGroupId(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        UserSpot userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny().orElse(null);
        return userSpot == null ? null : userSpot.getSpot().getGroup().getId();
    }

    @Named("getGroupName")
    default String getGroupName(List<UserSpot> userSpots) {
        if(userSpots.isEmpty()) return null;
        UserSpot userSpot = userSpots.stream().filter(UserSpot::getIsDefault).findAny().orElse(null);
        return userSpot == null ? null : userSpot.getSpot() instanceof MySpot ? null : userSpot.getSpot().getGroup().getName();
    }

}
