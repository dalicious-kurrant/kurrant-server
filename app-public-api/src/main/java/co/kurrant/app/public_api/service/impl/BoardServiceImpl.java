package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.kurrant.app.public_api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final NoticeRepository noticeRepository;
    private final QNoticeRepository qNoticeRepository;
    private final QCustomerBoardRepository qCustomerBoardRepository;

    @Override
    public Object noticeList(Integer type) {
        return qNoticeRepository.findAllByType(type);
    }

    @Override
    public Object customerBoardList() {
        return qCustomerBoardRepository.findAll();
    }
}
