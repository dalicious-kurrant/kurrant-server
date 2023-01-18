package co.kurrant.app.public_api.mapper.board;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.board.entity.Alarm;
import co.kurrant.app.public_api.dto.board.AlarmDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlarmMapper extends GenericMapper<AlarmDto, Alarm> {

    AlarmDto toDto(Alarm alarm);

}
