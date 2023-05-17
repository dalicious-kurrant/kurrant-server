package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface BoardService {
    List<NoticeDto> noticeList(Integer status, BigInteger spotId);

    List<CustomerServiceDto> customerBoardList();

    List<PushResponseDto> alarmBoardList(SecurityUser securityUser);

    void deleteAllAlarm(SecurityUser securityUser);
}
