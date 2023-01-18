package co.kurrant.app.public_api.mapper.board;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.board.entity.Alarm;
import co.kurrant.app.public_api.dto.board.AlarmDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlarmMapper extends GenericMapper<AlarmDto, Alarm> {

    @Mapping(source="alarm.user", target = "userId")
    AlarmDto toDto(Alarm alarm);

}
