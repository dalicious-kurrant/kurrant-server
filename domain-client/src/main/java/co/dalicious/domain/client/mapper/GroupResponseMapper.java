package co.dalicious.domain.client.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring")
public interface GroupResponseMapper {

    @Mapping(source = "address", target = "address", qualifiedByName = "addressToString")
    @Mapping(source = "group", target = "spotType", qualifiedByName = "setSpotType")
    OpenGroupResponseDto toOpenGroupDto(Group group);

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
