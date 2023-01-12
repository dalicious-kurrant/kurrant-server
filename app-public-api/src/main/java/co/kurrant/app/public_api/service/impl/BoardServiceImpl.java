package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.kurrant.app.public_api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final NoticeRepository noticeRepository;
    private final QNoticeRepository qNoticeRepository;

    @Override
    public Object noticeList(Integer type) {
        PageRequest pageRequest = PageRequest.of(0,5);
        return qNoticeRepository.findAllByType(pageRequest, type);
    }
}
