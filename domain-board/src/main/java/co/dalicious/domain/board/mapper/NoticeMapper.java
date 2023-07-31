package co.dalicious.domain.board.mapper;

import co.dalicious.domain.board.dto.AppBoardRequestDto;
import co.dalicious.domain.board.dto.AppBoardResponseDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {DateUtils.class, BoardType.class})
public interface NoticeMapper {

    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    @Mapping(target = "isPushAlarm", defaultValue = "false")
    Notice toNotice(AppBoardRequestDto requestDto);

    default List<AppBoardResponseDto> toDto(Page<Notice> notices, Map<BigInteger, String> groupNameMap){
        List<AppBoardResponseDto> appBoardResponseDtos = new ArrayList<>();

        for (Notice notice : notices) {
            AppBoardResponseDto appBoardResponseDto = new AppBoardResponseDto();

            appBoardResponseDto.setId(notice.getId());
            appBoardResponseDto.setTitle(notice.getTitle());
            appBoardResponseDto.setContent(notice.getContent());
            appBoardResponseDto.setBoardType(notice.getBoardType().getCode());
            appBoardResponseDto.setGroupNames(notice.getGroupIds() == null || notice.getGroupIds().isEmpty() ? null : groupNameMap.keySet().stream().filter(v -> notice.getGroupIds().contains(v)).map(groupNameMap::get).toList());
            appBoardResponseDto.setIsStatus(notice.getIsStatus());
            appBoardResponseDto.setIsPushAlarm(notice.getIsPushAlarm());
            appBoardResponseDto.setCreateDate(DateUtils.toISOLocalDate(notice.getCreatedDateTime()));

            appBoardResponseDtos.add(appBoardResponseDto);
        }

        return appBoardResponseDtos;
    }

    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    void updateNotice(AppBoardRequestDto requestDto, @MappingTarget Notice notice);

    @Mapping(source = "notice.createdDateTime", target = "created", qualifiedByName = "timeFormat")
    @Mapping(source = "notice.updatedDateTime", target = "updated", qualifiedByName = "timeFormat")
    @Mapping(source = "notice.isStatus", target = "status")
    NoticeDto toDto(Notice notice);

    @Named("timeFormat")
    default String timeFormat(Timestamp date){
        return DateUtils.format(date, "yyyy-MM-dd");
    }

    @Named("generatedStatus")
    default Integer generatedStatus(BoardType status){
        return status.getCode();
    }
}
