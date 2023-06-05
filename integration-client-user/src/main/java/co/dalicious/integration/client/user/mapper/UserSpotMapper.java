package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSpotMapper {

    default UserSpot toUserSpot(Spot spot, User user, Boolean isDefault, ClientType clientType){
        return UserSpot.builder()
                .user(user)
                .spot(spot)
                .clientType(clientType)
                .isDefault(isDefault)
                .build();
    };
}
