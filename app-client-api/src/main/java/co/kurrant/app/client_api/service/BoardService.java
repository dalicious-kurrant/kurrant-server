package co.kurrant.app.client_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.kurrant.app.client_api.model.SecurityUser;

public interface BoardService {
    ListItemResponseDto<NoticeDto> getClientBoard(SecurityUser securityUser, Integer type, OffsetBasedPageRequest pageable);
}
