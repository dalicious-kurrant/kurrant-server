package co.kurrant.app.public_api.mapper.board;

import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardStatus;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface NoticeMapper{
    @Mapping(source = "notice.createdDateTime", target = "created", qualifiedByName = "timeFormat")
    @Mapping(source = "notice.updatedDateTime", target = "updated", qualifiedByName = "timeFormat")
    @Mapping(source = "notice.status", target = "status", qualifiedByName = "generatedStatus")
    NoticeDto toDto(Notice notice);

    @Named("timeFormat")
    default String timeFormat(Timestamp date){
        return DateUtils.format(date, "yyyy-MM-dd");
    }

    @Named("generatedStatus")
    default Integer generatedStatus(BoardStatus status){
        return status.getCode();
    }

}
