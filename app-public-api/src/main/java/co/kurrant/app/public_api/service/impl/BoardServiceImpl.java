package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.sse.SseService;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.board.entity.CustomerService;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.NoticeDto;
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
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final QNoticeRepository qNoticeRepository;
    private final QCustomerBoardRepository qCustomerBoardRepository;
    private final NoticeMapper noticeMapper;
    private final PushAlarmHashRepository pushAlarmHashRepository;
    private final CustomerServiceMapper customerServiceMapper;
    private final UserUtil userUtil;
    private final QGroupRepository qGroupRepository;
    private final SseService sseService;

    @Override
    @Transactional
    public List<NoticeDto>  noticeList(Integer status, BigInteger groupId, SecurityUser securityUser) {
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


        List<PushAlarmHash> pushAlarmHashes = pushAlarmHashRepository.findAllPushAlarmHashByUserIdAndIsRead(securityUser.getId(), false);
        if(!pushAlarmHashes.isEmpty()) sseService.send(securityUser.getId(), 6, null, null, null);

        return result.stream().sorted(Comparator.comparing(NoticeDto::getCreated).reversed()).toList();
    }

    @Override
    @Transactional
    public List<CustomerServiceDto> customerBoardList() {
        List<CustomerServiceDto> result = new ArrayList<>();
        List<CustomerService> customerServiceList = qCustomerBoardRepository.findAll();
        for (CustomerService customerService:customerServiceList){
            result.add(customerServiceMapper.toDto(customerService));
        }
        return result;
    }

    @Override
    @Transactional
    public List<PushResponseDto> alarmBoardList(SecurityUser securityUser) {
        List<PushAlarmHash> pushAlarmHashes = pushAlarmHashRepository.findAllByUserIdOrderByCreatedDateTimeDesc(securityUser.getId());
        List<PushResponseDto> alarmResponseDtos = new ArrayList<>();
        for (PushAlarmHash pushAlarmHash : pushAlarmHashes) {
            alarmResponseDtos.add(new PushResponseDto(pushAlarmHash));
        }
        return alarmResponseDtos;
    }

    @Override
    @Transactional
    public void deleteAllAlarm(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        List<PushAlarmHash> pushAlarmHashes = pushAlarmHashRepository.findAllByUserIdOrderByCreatedDateTimeDesc(user.getId());
        pushAlarmHashRepository.deleteAll(pushAlarmHashes);
    }

    @Override
    @Transactional
    public void readAllAlarm(SecurityUser securityUser, List<String> ids) {
        User user = userUtil.getUser(securityUser);
        List<PushAlarmHash> pushAlarmHashes = ids.stream().map(id ->pushAlarmHashRepository.findAllPushAlarmHashByUserIdAndId(user.getId(), id)).toList();
        pushAlarmHashes.forEach(v -> v.updateRead(true));
    }
}
