package co.kurrant.app.public_api.mapper.user;

import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.kurrant.app.public_api.dto.user.UserHomeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.transaction.Transactional;
import java.math.BigInteger;


@Mapper(componentModel = "spring")
@Transactional
public interface UserHomeInfoMapper {
    @Mapping(source = "userSpot", target = "spotType", qualifiedByName = "getSpotTypeCode")
    @Mapping(source = "userSpot", target = "spotId", qualifiedByName = "getSpotId")
    @Mapping(source = "userSpot", target = "spot", qualifiedByName = "getSpotName")
    UserHomeResponseDto toDto(User user);

    @Named("getSpotTypeCode")
    default Integer getSpotTypeCode(UserSpot userSpot) {
        if(userSpot == null) return null;
        return userSpot.getClientType().getCode();
    }

    @Named("getSpotId")
    default BigInteger getSpotId(UserSpot userSpot) {
        if(userSpot == null) return null;
        return (userSpot.getClientType() == ClientType.APARTMENT) ?
                userSpot.getApartmentSpot().getId() : userSpot.getCorporationSpot().getId();
    }

    @Named("getSpotName")
    default String getSpotName(UserSpot userSpot) {
        if(userSpot == null) return null;
        return (userSpot.getClientType() == ClientType.APARTMENT) ?
                userSpot.getApartmentSpot().getName() : userSpot.getCorporationSpot().getName();
    }
}
