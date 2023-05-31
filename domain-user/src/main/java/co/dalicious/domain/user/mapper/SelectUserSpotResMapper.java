package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.SelectUserSpotResDto;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SelectUserSpotResMapper {
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "clientType.code", target = "clientType")
    @Mapping(source = "clientType", target = "hasHo", qualifiedByName = "hasHo")
    SelectUserSpotResDto toDto(UserSpot userSpot);

    @Named("hasHo")
    default Boolean hasHo(ClientType clientType) {
        return clientType == ClientType.MY_SPOT;
    }
}
