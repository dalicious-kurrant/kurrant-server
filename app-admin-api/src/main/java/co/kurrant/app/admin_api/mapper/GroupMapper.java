package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.DiningType;
import co.kurrant.app.admin_api.dto.GroupDto;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = GroupDto.class)
public interface GroupMapper {
    @Mapping(source = "id", target = "groupId")
    @Mapping(source = "name", target = "groupName")
    @Named("toIdAndNameDto")
    GroupDto.Group groupToDto(Group group);

    @Mapping(source = "id", target = "spotId")
    @Mapping(source = "name", target = "spotName")
    GroupDto.Spot spotToDto(Spot spot);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "code", target = "code")
    GroupDto.DiningType diningTypeToDto (DiningType diningType);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "name", target = "userName")
    GroupDto.User userToDto(User user);

    default List<GroupDto.Group> groupsToDtos(List<? extends Group> groups) {
        return groups.stream()
                .map(this::groupToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.Spot> spotsToDtos(List<Spot> spots) {
        return spots.stream()
                .map(this::spotToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.DiningType> diningTypesToDtos(List<DiningType> diningTypes) {
        return diningTypes.stream()
                .map(this::diningTypeToDto)
                .collect(Collectors.toList());
    }

    default List<GroupDto.User> usersToDtos(List<User> users) {
        return users.stream()
                .map(this::userToDto)
                .collect(Collectors.toList());
    }

    default GroupDto groupToGroupDto(Group group, List<User> users) {
        GroupDto groupDto = new GroupDto();
        groupDto.setGroup(groupToDto(group));
        groupDto.setSpots(spotsToDtos(group.getSpots()));
        groupDto.setDiningTypes(diningTypesToDtos(group.getDiningTypes()));
        groupDto.setUsers(usersToDtos(users));
        return groupDto;
    }
}
