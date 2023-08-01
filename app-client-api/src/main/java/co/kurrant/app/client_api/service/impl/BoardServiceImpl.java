package co.kurrant.app.client_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.dalicious.domain.board.entity.ClientNotice;
import co.dalicious.domain.board.entity.MakersNotice;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.board.mapper.BackOfficeNoticeMapper;
import co.dalicious.domain.board.repository.QBackOfficeNoticeRepository;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.food.entity.Makers;
import co.kurrant.app.client_api.mapper.ArticleMapper;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.BoardService;
import co.kurrant.app.client_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {
    private final UserUtil userUtil;
    private final QBackOfficeNoticeRepository qBackOfficeNoticeRepository;
    private final BackOfficeNoticeMapper backOfficeNoticeMapper;

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<NoticeDto> getClientBoard(SecurityUser securityUser, Integer type, OffsetBasedPageRequest pageable) {
        Corporation corporation = userUtil.getCorporation(securityUser);
        BoardType boardType = BoardType.ofCode(type);

        Page<ClientNotice> clientNotices = qBackOfficeNoticeRepository.findClientNoticeAllByClientIdAndType(corporation.getId(), boardType, pageable);

        if(clientNotices.isEmpty()) ListItemResponseDto.<NoticeDto>builder().items(null).limit(pageable.getPageSize()).offset(pageable.getOffset()).count(0).total((long) clientNotices.getTotalPages()).build();

        List<NoticeDto> noticeDtos = backOfficeNoticeMapper.getClientNoticeDtoList(clientNotices);

        return ListItemResponseDto.<NoticeDto>builder().items(noticeDtos).limit(pageable.getPageSize()).offset(pageable.getOffset())
                .count(clientNotices.getNumberOfElements()).total((long) clientNotices.getTotalPages()).isLast(clientNotices.isLast()).build();
    }
}
