package co.dalicious.domain.user.mapper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.client.entity.MySpot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserSpotMapper {

    @Mapping(target = "id", ignore = true)
    UserSpot toUserSpot(Spot spot, User user, Boolean isDefault, GroupDataType groupDataType);

    default List<UserSpot> toEntityList(List<MySpot> spots, List<User> users, GroupDataType groupDataType, Boolean isDefault) {
        List<UserSpot> userSpots = new ArrayList<>();

        users.forEach(user -> {
            Optional<MySpot> spot = spots.stream()
                    .filter(v ->  v.getUserId().equals(user.getId()))
                    .findAny();
            spot.ifPresent(s -> userSpots.add(toUserSpot(s, user, isDefault, groupDataType)));
        });
        return userSpots;
    }
}
