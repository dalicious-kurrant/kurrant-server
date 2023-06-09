package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSpotMapper {

    default UserSpot toUserSpot(Spot spot, User user, Boolean isDefault, GroupDataType groupDataType){
        return UserSpot.builder()
                .user(user)
                .spot(spot)
                .groupDataType(groupDataType)
                .isDefault(isDefault)
                .build();
    };
}
