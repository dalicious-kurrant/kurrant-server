package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.board.entity.Alarm;
import co.dalicious.domain.board.entity.CustomerService;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.repository.QAlarmRepository;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.dto.board.AlarmResponseDto;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;
import co.kurrant.app.public_api.mapper.board.AlarmMapper;
import co.kurrant.app.public_api.mapper.board.CustomerServiceMapper;
import co.kurrant.app.public_api.mapper.board.NoticeMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final QGroupRepository qGroupRepository;

    @Override
    public List<NoticeDto>  noticeList(Integer status, BigInteger groupId) {
        List<NoticeDto> result = new ArrayList<>();
        List<Notice> noticeList = qNoticeRepository.findAllByType(status);


        //status가 3이고 스팟ID가 NULL이 아니면 스팟공지만 리턴
        if (status == 3 && groupId != null){

            //스팟 검증
            BigInteger findGroupId = qGroupRepository.findById(groupId);
            if (findGroupId != null){
                List<Notice> spotNoticeList = qNoticeRepository.findAllSpotNotice(findGroupId);
                for (Notice notice:spotNoticeList){
                    result.add(noticeMapper.toDto(notice));
                }
                return result;
            } else{
                throw new ApiException(ExceptionEnum.GROUP_NOT_FOUND);
            }
        }

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
    @Transactional
    public Long deleteAllAlarm(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        return qAlarmRepository.deleteAllAlarm(user.getId());
    }
}
