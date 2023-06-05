package co.dalicious.domain.order.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.integration.client.user.entity.MySpot;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderUserInfoMapper {
    @Mapping(source = "userSpots", target = "groupName", qualifiedByName = "getDefaultGroupName")
    @Mapping(source = "userSpots", target = "spotName", qualifiedByName = "getDefaultSpotName")
    @Mapping(source = "userSpots", target = "address", qualifiedByName = "getAddress")
    @Mapping(target = "user", qualifiedByName = "setUser")
    OrderUserInfoDto toDto(User user);

    @Named("getDefaultGroupName")
    default String getDefaultGroupName(List<UserSpot> userSpots) {
        UserSpot userDefaultSpot = null;
        for (UserSpot userSpot : userSpots) {
            if (userSpot.getIsDefault()) {
                userDefaultSpot = userSpot;
            }
        }
        if (userDefaultSpot != null) {
            return userDefaultSpot.getSpot().getGroup().getName();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }

    @Named("getDefaultSpotName")
    default String getDefaultSpotName(List<UserSpot> userSpots) {
        UserSpot userDefaultSpot = null;
        for (UserSpot userSpot : userSpots) {
            if (userSpot.getIsDefault()) {
                userDefaultSpot = userSpot;
            }
        }
        if (userDefaultSpot != null) {
            return userDefaultSpot.getSpot().getName();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }

    @Named("getAddress")
    default Address getAddress(List<UserSpot> userSpots) {
        UserSpot userDefaultSpot = null;
        for (UserSpot userSpot : userSpots) {
            if (userSpot.getIsDefault()) {
                userDefaultSpot = userSpot;
            }
        }
        if (userDefaultSpot != null) {
            return userDefaultSpot.getSpot().getAddress();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }

    @Named("setUser")
    default User setUser(User user) {
        return user;
    }

}
