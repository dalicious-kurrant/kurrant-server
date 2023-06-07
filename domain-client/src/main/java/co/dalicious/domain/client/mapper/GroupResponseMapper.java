package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.system.enums.DiningType;
import org.hibernate.Hibernate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = DiningType.class)
public interface GroupResponseMapper {

    @Mapping(source = "group.address", target = "address", qualifiedByName = "addressToString")
    @Mapping(target = "diningType", expression = "java(group.getDiningTypes().stream().map(DiningType::getCode).toList())")
    @Mapping(source = "group", target = "spotType", qualifiedByName = "setSpotType")
    @Mapping(source = "group.openGroupUserCount", target = "userCount")
    OpenGroupResponseDto toOpenGroupDto(OpenGroup group, Double distance) ;

    @AfterMapping
    default void toLocation(OpenGroup group, @MappingTarget OpenGroupResponseDto dto) {
        String location = group.getAddress().locationToString();

        dto.setLatitude(location.split(" ")[0]);
        dto.setLongitude(location.split(" ")[1]);
    }

    @Named("addressToString")
    default String addressToString(Address address) {
        return address.addressToString();
    }

    @Named("setSpotType")
    default Integer setSpotType(Group group) {
        Integer code = null;
        if(Hibernate.unproxy(group) instanceof Corporation) code = GroupDataType.CORPORATION.getCode();
        if(Hibernate.unproxy(group) instanceof Apartment) code = GroupDataType.MY_SPOT.getCode();
        if(Hibernate.unproxy(group) instanceof OpenGroup) code = GroupDataType.OPEN_GROUP.getCode();

        return code;
    }
}
