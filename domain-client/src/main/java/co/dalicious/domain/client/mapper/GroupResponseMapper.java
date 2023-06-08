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


}
