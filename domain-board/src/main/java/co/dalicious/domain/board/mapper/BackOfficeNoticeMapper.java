package co.dalicious.domain.board.mapper;

import co.dalicious.domain.board.dto.MakersBoardRequestDto;
import co.dalicious.domain.board.entity.BackOfficeNotice;
import co.dalicious.domain.board.entity.MakersNotice;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {DateUtils.class, BoardType.class})
public interface BackOfficeNoticeMapper {

    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    @Mapping(target = "isAlarmTalk", defaultValue = "false")
    MakersNotice toMakersNotice(MakersBoardRequestDto requestDto);
}
