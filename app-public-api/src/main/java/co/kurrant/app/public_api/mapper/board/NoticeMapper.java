package co.kurrant.app.public_api.mapper.board;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.board.entity.Notice;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface NoticeMapper extends GenericMapper<NoticeDto, Notice> {

    NoticeDto toDto(Notice notice);

}
