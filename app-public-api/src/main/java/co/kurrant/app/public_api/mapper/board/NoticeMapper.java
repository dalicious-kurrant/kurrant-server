package co.kurrant.app.public_api.mapper.board;

import co.dalicious.domain.board.entity.Notice;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoticeMapper{

    NoticeDto toDto(Notice notice);

}
