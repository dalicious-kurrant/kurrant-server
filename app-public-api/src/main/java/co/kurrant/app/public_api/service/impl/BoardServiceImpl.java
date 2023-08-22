package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.pubsub.SseService;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.board.dto.NoticeDto;
import co.dalicious.domain.board.entity.CustomerService;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.board.mapper.NoticeMapper;
import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.kurrant.app.public_api.dto.board.CustomerServiceDto;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.mapper.board.CustomerServiceMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.BoardService;
import co.kurrant.app.public_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NoticeRepository noticeRepository;

    @Override
    @Transactional(readOnly = true)
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
        if (pushAlarmHashes.isEmpty()) return;
        for (PushAlarmHash pushAlarmHash : pushAlarmHashes) {
            if (ids.contains(pushAlarmHash.getId())) {
                pushAlarmHash.setRead(true);
                pushAlarmHashRepository.save(pushAlarmHash);
            }
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<NoticeDto> noticeList(SecurityUser securityUser, Integer type, OffsetBasedPageRequest pageable) {
        // 유저가 속한 그룹이 맞는지 확인
        User user = userUtil.getUser(securityUser);
        List<BigInteger> uesrGroupList = null;
        if(type != null && type.equals(BoardType.SPOT.getCode())) {
            uesrGroupList = user.getGroups().stream().filter(v -> v.getClientStatus().equals(ClientStatus.BELONG)).map(v -> v.getGroup().getId()).toList();
        }

        Page<Notice> noticeList = qNoticeRepository.findAllNoticeBySpotFilter(uesrGroupList, pageable);
        List<NoticeDto> noticeDtos = new ArrayList<>();
        if(noticeList.isEmpty()) {
            return ListItemResponseDto.<NoticeDto>builder().items(noticeDtos).count(0).limit(pageable.getPageSize()).offset(pageable.getOffset()).total((long) noticeList.getTotalPages()).isLast(true).build();
        }

        for (Notice notice : noticeList){
            noticeDtos.add(noticeMapper.toDto(notice));
        }

        return ListItemResponseDto.<NoticeDto>builder().items(noticeDtos).count(noticeList.getNumberOfElements()).limit(pageable.getPageSize())
                .offset(pageable.getOffset()).total((long) noticeList.getTotalPages()).isLast(noticeList.isLast()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDto getNoticeDetail(SecurityUser securityUser, BigInteger noticeId) {
        Notice notice = noticeRepository.findByIdAndIsStatus(noticeId, true);
        if(notice == null) throw new ApiException(ExceptionEnum.NOTICE_NOT_FOUND);

        return noticeMapper.toDto(notice);
    }
}
