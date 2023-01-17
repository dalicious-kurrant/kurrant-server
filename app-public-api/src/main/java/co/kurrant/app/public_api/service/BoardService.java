package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;

import java.util.List;

public interface BoardService {
    List<NoticeDto> noticeList(Integer type);

    List<CustomerServiceDto> customerBoardList();
}
