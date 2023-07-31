package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface BoardService {
    List<NoticeDto> popupNoticeList(SecurityUser securityUser);
    List<CustomerServiceDto> customerBoardList();
    List<PushResponseDto> alarmBoardList(SecurityUser securityUser);
    void deleteAllAlarm(SecurityUser securityUser);
    void readAllAlarm(SecurityUser securityUser, List<String> ids);
    List<NoticeDto> noticeList(SecurityUser securityUser, BigInteger groupId);
}
