package co.dalicious.domain.client.mapper;

import co.dalicious.system.enums.DiningType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = DiningType.class)
public interface GroupResponseMapper {


}
