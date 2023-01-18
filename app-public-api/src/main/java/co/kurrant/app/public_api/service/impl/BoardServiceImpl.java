package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.board.entity.Alarm;
import co.dalicious.domain.board.entity.CustomerService;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.AlarmBoardType;
import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QAlarmRepository;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.board.AlarmResponseDto;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import co.kurrant.app.public_api.mapper.board.AlarmMapper;
import co.kurrant.app.public_api.mapper.board.CustomerServiceMapper;
import co.kurrant.app.public_api.mapper.board.NoticeMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final QNoticeRepository qNoticeRepository;
    private final QCustomerBoardRepository qCustomerBoardRepository;
    private final QAlarmRepository qAlarmRepository;
    private final NoticeMapper noticeMapper;
    private final AlarmMapper alarmMapper;
    private final CustomerServiceMapper customerServiceMapper;
    private final UserUtil userUtil;

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

    @Override
    public List<AlarmResponseDto> alarmBoardList(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        List<AlarmResponseDto> result = new ArrayList<>();
        List<Alarm> alarmList = qAlarmRepository.findAllByUserId(user.getId());
        for (Alarm alarm : alarmList){
           result.add(alarmMapper.toDto(alarm));
        }
        return result;
    }

    @Override
    public Long deleteAllAlarm(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        return qAlarmRepository.deleteAllAlarm(user.getId());
    }
}
