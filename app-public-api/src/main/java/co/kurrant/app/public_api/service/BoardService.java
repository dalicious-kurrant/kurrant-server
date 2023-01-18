package co.kurrant.app.public_api.service;

import co.dalicious.domain.board.entity.Alarm;
import co.kurrant.app.public_api.dto.board.AlarmDto;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.util.List;

public interface BoardService {
    List<NoticeDto> noticeList(Integer type);

    List<CustomerServiceDto> customerBoardList();

    List<AlarmDto> alarmBoardList(SecurityUser securityUser);

    Long deleteAllAlarm(SecurityUser securityUser);
}
