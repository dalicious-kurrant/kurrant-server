package co.dalicious.domain.client.mapper;

import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MySpotZoneMapper {

}
