package co.kurrant.app.makers_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.dalicious.domain.board.entity.MakersNotice;
import co.dalicious.domain.board.entity.enums.BoardCategory;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.board.mapper.BackOfficeNoticeMapper;
import co.dalicious.domain.board.repository.QBackOfficeNoticeRepository;
import co.dalicious.domain.food.entity.Makers;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.BoardService;
import co.kurrant.app.makers_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserUtil userUtil;
    private final QBackOfficeNoticeRepository qBackOfficeNoticeRepository;
    private final BackOfficeNoticeMapper backOfficeNoticeMapper;
    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<NoticeDto> getMakersBoard(SecurityUser securityUser, Integer type, OffsetBasedPageRequest pageable) {
        Makers makers = userUtil.getMakers(securityUser);
        List<BoardType> categories = BoardCategory.getBoardTypeByCategory(BoardCategory.ofCode(type));

        Page<MakersNotice> makersNotices = qBackOfficeNoticeRepository.findMakersNoticeAllByMakersIdAndType(makers.getId(), categories, pageable);

        if(makersNotices.isEmpty()) ListItemResponseDto.<NoticeDto>builder().items(null).limit(pageable.getPageSize()).offset(pageable.getOffset()).count(0).total((long) makersNotices.getTotalPages()).build();

        List<NoticeDto> noticeDtos = backOfficeNoticeMapper.getMakersNoticeDtoList(makersNotices);

        return ListItemResponseDto.<NoticeDto>builder().items(noticeDtos).limit(pageable.getPageSize()).offset(pageable.getOffset())
                .count(makersNotices.getNumberOfElements()).total((long) makersNotices.getTotalPages()).isLast(makersNotices.isLast()).build();
    }
}
