package co.dalicious.domain.user.mapper;

import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.dto.SelectUserSpotResDto;
import co.dalicious.domain.user.entity.UserSpot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SelectUserSpotResMapper {
    @Mapping(source = "spot.id", target = "spotId")
    @Mapping(source = "groupDataType.code", target = "clientType")
    @Mapping(source = "groupDataType", target = "hasHo", qualifiedByName = "hasHo")
    SelectUserSpotResDto toDto(UserSpot userSpot);

    @Named("hasHo")
    default Boolean hasHo(GroupDataType clientType) {
        return clientType == GroupDataType.MY_SPOT;
    }
}
