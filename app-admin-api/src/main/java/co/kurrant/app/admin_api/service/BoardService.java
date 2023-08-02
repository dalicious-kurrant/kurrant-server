package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface BoardService {
    void createAppBoard(AppBoardRequestDto requestDto);
    ListItemResponseDto<AppBoardResponseDto> getAppBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    void updateAppBoard(BigInteger noticeId, AppBoardRequestDto requestDto);
    void postPushAlarm(BigInteger noticeId);
    void createMakersBoard(MakersBoardRequestDto requestDto);
    ListItemResponseDto<MakersBoardResponseDto> getMakersBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    void updateMakersBoard(BigInteger noticeId, MakersBoardRequestDto requestDto);
    void createClientBoard(ClientBoardRequestDto requestDto);
    ListItemResponseDto<ClientBoardResponseDto> getClientBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    void updateClientBoard(BigInteger noticeId, ClientBoardRequestDto requestDto);
    void postAlarmTalk(BigInteger noticeId);

}
