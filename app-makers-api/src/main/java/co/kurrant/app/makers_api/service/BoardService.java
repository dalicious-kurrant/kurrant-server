package co.kurrant.app.makers_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.util.Map;

public interface BoardService {
    ListItemResponseDto<NoticeDto> getMakersBoard(SecurityUser securityUser, Integer type, OffsetBasedPageRequest pageable);
}
