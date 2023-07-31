package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.dto.AppBoardRequestDto;
import co.dalicious.domain.board.dto.AppBoardResponseDto;
import co.dalicious.domain.board.dto.MakersBoardRequestDto;

import java.math.BigInteger;
import java.util.Map;

public interface BoardService {
    void createAppBoard(AppBoardRequestDto requestDto);
    ListItemResponseDto<AppBoardResponseDto> getAppBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    void updateAppBoard(BigInteger noticeId, AppBoardRequestDto requestDto);
    void postPushAlarm(BigInteger noticeId);
    void createMakersBoard(MakersBoardRequestDto requestDto);
    ListItemResponseDto<AppBoardResponseDto> getMakersBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    void updateMakersBoard(BigInteger noticeId, MakersBoardRequestDto requestDto);

}
