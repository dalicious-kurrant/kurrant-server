package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.system.enums.DiningType;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring", imports = DiningType.class)
public interface GroupResponseMapper {

    @Mapping(source = "group.address", target = "address", qualifiedByName = "addressToString")
    @Mapping(target = "latitude", expression = "java(String.valueOf(group.getAddress().getLocation()).split(\" \")[0])")
    @Mapping(target = "longitude", expression = "java(String.valueOf(group.getAddress().getLocation()).split(\" \")[1])")
    @Mapping(target = "diningType", expression = "java(group.getDiningTypes().stream().map(DiningType::getCode).toList())")
    @Mapping(source = "group", target = "spotType", qualifiedByName = "setSpotType")
    @Mapping(source = "group.openGroupUserCount", target = "userCount")
    OpenGroupResponseDto toOpenGroupDto(OpenGroup group, Double distance);

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
