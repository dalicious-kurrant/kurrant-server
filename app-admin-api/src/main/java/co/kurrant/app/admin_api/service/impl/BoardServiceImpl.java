package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.client.sse.SseService;
import co.dalicious.domain.board.dto.AppBoardRequestDto;
import co.dalicious.domain.board.dto.AppBoardResponseDto;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.board.mapper.NoticeMapper;
import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.service.BoardService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;
    private final QNoticeRepository qNoticeRepository;
    private final QGroupRepository qGroupRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final PushUtil pushUtil;
    private final PushService pushService;
    private final SseService sseService;
    private final QUserRepository qUserRepository;

    @Override
    @Transactional
    public void createAppBoard(AppBoardRequestDto requestDto) {
        Notice notice = noticeMapper.toNotice(requestDto);
        noticeRepository.save(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<AppBoardResponseDto> getAppBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        List<BigInteger> groupIds = !parameters.containsKey("groupIds") || parameters.get("groupIds") == null ? null : StringUtils.parseBigIntegerList((String) parameters.get("groupIds"));
        Boolean isStatus = !parameters.containsKey("isStatus") || parameters.get("isStatus") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isStatus")));
        Boolean isPushAlarm = !parameters.containsKey("isPushAlarm") || parameters.get("isPushAlarm") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isPushAlarm")));
        BoardType boardType = !parameters.containsKey("boardType") || parameters.get("boardType") == null ? null : BoardType.ofCode(Integer.parseInt(String.valueOf(parameters.get("boardType"))));

        Page<Notice> noticeList = qNoticeRepository.findAllByParameters(groupIds, boardType, isStatus, isPushAlarm, pageable);

        if(noticeList.isEmpty()) ListItemResponseDto.<AppBoardResponseDto>builder().items(null).limit(pageable.getPageSize()).offset(pageable.getOffset()).count(0).total((long) noticeList.getTotalPages()).build();

        Map<BigInteger, String> groupNameMap = qGroupRepository.findGroupNameByIds(noticeList.stream().filter(v -> v.getGroupIds() != null).flatMap(v -> v.getGroupIds().stream()).collect(Collectors.toSet()));
        List<AppBoardResponseDto> appBoardResponseDtos = noticeMapper.toDto(noticeList, groupNameMap);

        return ListItemResponseDto.<AppBoardResponseDto>builder().items(appBoardResponseDtos).limit(pageable.getPageSize()).offset(pageable.getOffset())
                .count(noticeList.getNumberOfElements()).total((long) noticeList.getTotalPages()).isLast(noticeList.isLast()).build();
    }

    @Override
    @Transactional
    public void updateAppBoard(BigInteger noticeId, AppBoardRequestDto requestDto) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        noticeMapper.updateNotice(requestDto, notice);
    }

    @Override
    @Transactional
    public void postPushAlarm(BigInteger noticeId) {
        Notice notice = noticeRepository.findByIdAndIsStatus(noticeId, true);
        if(notice == null) throw new ApiException(ExceptionEnum.NOTICE_NOT_FOUND);
        if(notice.getIsPushAlarm()) throw new ApiException(ExceptionEnum.ALREADY_SEND_PUSH_ALARM);

        int sseType;
        List<User> users;
        if(notice.getGroupIds() == null || notice.getGroupIds().isEmpty()) {
            users = qUserRepository.findAllByNotNullFirebaseToken();
            sseType = 1;
        } else {
            users = qUserGroupRepository.findAllUserByGroupIdsAadFirebaseTokenNotNull(notice.getGroupIds());
            sseType = 2;
        }

        String customMessage = pushUtil.getContextAppNotice(notice.getTitle(), PushCondition.NEW_NOTICE);
        List<PushRequestDtoByUser> pushRequestDtoByUserList = new ArrayList<>();
        for (User user : users) {
            PushRequestDtoByUser pushRequestDtoByUser = pushUtil.getPushRequest(user, PushCondition.NEW_NOTICE, customMessage);
            if(pushRequestDtoByUser == null) continue;
            pushRequestDtoByUserList.add(pushRequestDtoByUser);

            pushUtil.savePushAlarmHashByNotice(pushRequestDtoByUser.getTitle(), pushRequestDtoByUser.getMessage(), user.getId(), AlarmType.NOTICE, notice.getId());
            sseService.send(user.getId(), sseType, null, null, null);
        }

        pushService.sendToPush(pushRequestDtoByUserList);
        notice.updatePushAlarm(true);
    }
}
