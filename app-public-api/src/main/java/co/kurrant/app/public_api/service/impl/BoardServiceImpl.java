package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.sse.SseService;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.board.entity.CustomerService;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.mapper.NoticeMapper;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.dalicious.domain.board.dto.NoticeDto;
import co.kurrant.app.public_api.mapper.board.CustomerServiceMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Override
    @Transactional
    public List<NoticeDto>  popupNoticeList(SecurityUser securityUser) {
        List<NoticeDto> result = new ArrayList<>();
        List<Notice> noticeList = qNoticeRepository.findPopupNotice();
        for (Notice notice:noticeList){
           result.add(noticeMapper.toDto(notice));
        }

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
        List<PushAlarmHash> pushAlarmHashes = pushAlarmHashRepository.findAllByUserIdOrderByCreatedDateTimeDesc(securityUser.getId());
        if (!pushAlarmHashes.isEmpty()) pushAlarmHashes.stream()
                .filter(v -> ids.contains(v.getId()) && !v.getIsRead())
                .findAny()
                .ifPresent(v -> {
                    v.setRead(true);
                    pushAlarmHashRepository.save(v);
                });

    }
}
