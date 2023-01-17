package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.board.entity.CustomerService;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import co.kurrant.app.public_api.mapper.board.CustomerServiceMapper;
import co.kurrant.app.public_api.mapper.board.NoticeMapper;
import co.kurrant.app.public_api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final NoticeRepository noticeRepository;
    private final QNoticeRepository qNoticeRepository;
    private final QCustomerBoardRepository qCustomerBoardRepository;
    private final NoticeMapper noticeMapper;
    private final CustomerServiceMapper customerServiceMapper;

    @Override
    public List<NoticeDto>  noticeList(Integer type) {
        List<NoticeDto> result = new ArrayList<>();
        List<Notice> noticeList = qNoticeRepository.findAllByType(type);
        for (Notice notice:noticeList){
           result.add(noticeMapper.toDto(notice));
        }
        return result;
    }

    @Override
    public List<CustomerServiceDto> customerBoardList() {
        List<CustomerServiceDto> result = new ArrayList<>();
        List<CustomerService> customerServiceList = qCustomerBoardRepository.findAll();
        for (CustomerService customerService:customerServiceList){
            result.add(customerServiceMapper.toDto(customerService));
        }
        return result;
    }
}
