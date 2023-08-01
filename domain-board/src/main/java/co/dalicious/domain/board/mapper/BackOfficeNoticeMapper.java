package co.dalicious.domain.board.mapper;

import co.dalicious.domain.board.dto.*;
import co.dalicious.domain.board.entity.ClientNotice;
import co.dalicious.domain.board.entity.MakersNotice;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {DateUtils.class, BoardType.class})
public interface BackOfficeNoticeMapper {

    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    @Mapping(target = "isAlarmTalk", defaultValue = "false")
    MakersNotice toMakersNotice(MakersBoardRequestDto requestDto);

    default List<MakersBoardResponseDto> toMakersBoardResponseDto(Page<MakersNotice> notices, Map<BigInteger, String> makersNameMap){
        List<MakersBoardResponseDto> appBoardResponseDtos = new ArrayList<>();

        for (MakersNotice notice : notices) {
            MakersBoardResponseDto makersBoardResponseDto = new MakersBoardResponseDto();

            makersBoardResponseDto.setId(notice.getId());
            makersBoardResponseDto.setTitle(notice.getTitle());
            makersBoardResponseDto.setContent(notice.getContent());
            makersBoardResponseDto.setBoardType(notice.getBoardType().getCode());
            makersBoardResponseDto.setMakersName(notice.getMakersId() == null ? null : makersNameMap.keySet().stream().filter(v -> notice.getMakersId().equals(v)).map(makersNameMap::get).findAny().orElse(null));
            makersBoardResponseDto.setIsStatus(notice.getIsStatus() != null && notice.getIsStatus());
            makersBoardResponseDto.setIsAlarmTalk(notice.getIsAlarmTalk());
            makersBoardResponseDto.setCreateDate(DateUtils.toISOLocalDate(notice.getCreatedDateTime()));

            appBoardResponseDtos.add(makersBoardResponseDto);
        }

        return appBoardResponseDtos;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAlarmTalk", ignore = true)
    @Mapping(target = "updatedDateTime", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    void updateNotice(MakersBoardRequestDto requestDto, @MappingTarget MakersNotice notice);

    @Mapping(target = "created", expression = "java(DateUtils.toISOLocalDate(notice.getCreatedDateTime()))")
    @Mapping(target = "updated", expression = "java(DateUtils.toISOLocalDate(notice.getUpdatedDateTime()))")
    @Mapping(target = "boardType", expression = "java(notice.getBoardType().getCode())")
    @Mapping(source = "isStatus", target = "status")
    NoticeDto toDto(MakersNotice notice);

    default List<NoticeDto> getMakersNoticeDtoList(Page<MakersNotice> makersNotices) {
        return makersNotices.stream().map(this::toDto).toList();
    }

    default List<ClientBoardResponseDto> toClientBoardResponseDto(Page<ClientNotice> notices, Map<BigInteger, String> groupNameMap){
        List<ClientBoardResponseDto> boardResponseDtos = new ArrayList<>();

        for (ClientNotice notice : notices) {
            ClientBoardResponseDto boardResponseDto = new ClientBoardResponseDto();

            boardResponseDto.setId(notice.getId());
            boardResponseDto.setTitle(notice.getTitle());
            boardResponseDto.setContent(notice.getContent());
            boardResponseDto.setBoardType(notice.getBoardType().getCode());
            boardResponseDto.setGroupNames(notice.getGroupIds() == null || notice.getGroupIds().isEmpty() ? null : groupNameMap.keySet().stream().filter(v -> notice.getGroupIds().contains(v)).map(groupNameMap::get).toList());
            boardResponseDto.setIsStatus(notice.getIsStatus() != null && notice.getIsStatus());
            boardResponseDto.setIsAlarmTalk(notice.getIsAlarmTalk());
            boardResponseDto.setCreateDate(DateUtils.toISOLocalDate(notice.getCreatedDateTime()));

            boardResponseDtos.add(boardResponseDto);
        }

        return boardResponseDtos;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAlarmTalk", ignore = true)
    @Mapping(target = "updatedDateTime", ignore = true)
    @Mapping(target = "createdDateTime", ignore = true)
    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    void updateNotice(ClientBoardRequestDto requestDto, @MappingTarget ClientNotice notice);

    @Mapping(target = "created", expression = "java(DateUtils.toISOLocalDate(notice.getCreatedDateTime()))")
    @Mapping(target = "updated", expression = "java(DateUtils.toISOLocalDate(notice.getUpdatedDateTime()))")
    @Mapping(target = "boardType", expression = "java(notice.getBoardType().getCode())")
    @Mapping(source = "isStatus", target = "status")
    NoticeDto toDto(ClientNotice notice);

    default List<NoticeDto> getClientNoticeDtoList(Page<ClientNotice> clientNotices) {
        return clientNotices.stream().map(this::toDto).toList();
    }


    @Mapping(target = "boardType", expression = "java(BoardType.ofCode(requestDto.getBoardType()))")
    @Mapping(target = "isAlarmTalk", defaultValue = "false")
    ClientNotice toClientNotice(ClientBoardRequestDto requestDto);
}
