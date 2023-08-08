package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.AlimtalkRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.entity.enums.AlimTalkTemplate;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.KakaoUtil;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.data.redis.pubsub.SseService;
import co.dalicious.domain.board.dto.*;
import co.dalicious.domain.board.entity.BackOfficeNotice;
import co.dalicious.domain.board.entity.ClientNotice;
import co.dalicious.domain.board.entity.MakersNotice;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.entity.enums.BoardCategory;
import co.dalicious.domain.board.entity.enums.BoardType;
import co.dalicious.domain.board.entity.enums.NoticeType;
import co.dalicious.domain.board.mapper.BackOfficeNoticeMapper;
import co.dalicious.domain.board.mapper.NoticeMapper;
import co.dalicious.domain.board.repository.BackOfficeNoticeRepository;
import co.dalicious.domain.board.repository.NoticeRepository;
import co.dalicious.domain.board.repository.QBackOfficeNoticeRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.service.BoardService;
import com.querydsl.core.Tuple;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
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
    private final BackOfficeNoticeRepository backOfficeNoticeRepository;
    private final BackOfficeNoticeMapper backOfficeNoticeMapper;
    private final QBackOfficeNoticeRepository qBackOfficeNoticeRepository;
    private final QMakersRepository qMakersRepository;
    private final KakaoUtil kakaoUtil;

    @Override
    @Transactional
    public void createAppBoard(AppBoardRequestDto requestDto) {
        BoardType boardType = BoardType.ofCode(requestDto.getBoardType());
        if(!BoardType.showApp().contains(boardType)) throw new ApiException(ExceptionEnum.BAD_REQUEST);

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
        BoardType boardType = BoardType.ofCode(requestDto.getBoardType());
        if(!BoardType.showApp().contains(boardType)) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        noticeMapper.updateNotice(requestDto, notice);
    }

    @Override
    @Transactional
    public void postPushAlarm(BigInteger noticeId) {
        Notice notice = noticeRepository.findByIdAndIsStatus(noticeId, true);
        if(notice == null) throw new ApiException(ExceptionEnum.NOTICE_NOT_FOUND);
        if(notice.getIsPushAlarm()) throw new ApiException(ExceptionEnum.ALREADY_SEND_ALARM);

        int sseType;
        List<User> users;
        if(notice.getGroupIds() == null || notice.getGroupIds().isEmpty()) {
            users = qUserRepository.findAllByNotNullFirebaseToken();
            sseType = 1;
        } else {
            users = qUserGroupRepository.findAllUserByGroupIdsAadFirebaseTokenNotNull(notice.getGroupIds());
            sseType = 2;
        }

        System.out.println("notice.getTitle() = " + notice.getTitle());
        String customMessage = pushUtil.getContextAppNotice(notice.getTitle(), PushCondition.NEW_NOTICE);
        System.out.println("customMessage = " + customMessage);
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

    @Override
    @Transactional
    public void createMakersBoard(MakersBoardRequestDto requestDto) {
        BoardType boardType = BoardType.ofCode(requestDto.getBoardType());
        if(!BoardType.showMakers().contains(boardType)) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        MakersNotice notice = backOfficeNoticeMapper.toMakersNotice(requestDto);
        notice.setBoardCategory(BoardCategory.getBoardTypeByCategory(BoardType.ofCode(requestDto.getBoardType())));
        backOfficeNoticeRepository.save(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<MakersBoardResponseDto> getMakersBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId") == null ? null : BigInteger.valueOf(Long.parseLong(String.valueOf(parameters.get("makersId"))));
        Boolean isStatus = !parameters.containsKey("isStatus") || parameters.get("isStatus") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isStatus")));
        Boolean isAlarmTalk = !parameters.containsKey("isAlarmTalk") || parameters.get("isAlarmTalk") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isAlarmTalk")));
        BoardType boardType = !parameters.containsKey("boardType") || parameters.get("boardType") == null ? null : BoardType.ofCode(Integer.parseInt(String.valueOf(parameters.get("boardType"))));

        Page<MakersNotice> backOfficeNoticeList = qBackOfficeNoticeRepository.findAllByParameters(makersId, isStatus, isAlarmTalk, boardType, pageable);

        if(backOfficeNoticeList.isEmpty()) ListItemResponseDto.<MakersBoardResponseDto>builder().items(null).limit(pageable.getPageSize()).offset(pageable.getOffset()).count(0).total((long) backOfficeNoticeList.getTotalPages()).build();

        Map<BigInteger, String> makersNameMap = qMakersRepository.findByIdMapIdAndName(backOfficeNoticeList.stream().filter(v-> v.getMakersId() != null).map(MakersNotice::getMakersId).collect(Collectors.toSet()));
        List<MakersBoardResponseDto> appBoardResponseDtos = backOfficeNoticeMapper.toMakersBoardResponseDto(backOfficeNoticeList, makersNameMap);

        return ListItemResponseDto.<MakersBoardResponseDto>builder().items(appBoardResponseDtos).limit(pageable.getPageSize()).offset(pageable.getOffset())
                .count(backOfficeNoticeList.getNumberOfElements()).total((long) backOfficeNoticeList.getTotalPages()).isLast(backOfficeNoticeList.isLast()).build();
    }

    @Override
    @Transactional
    public void updateMakersBoard(BigInteger noticeId, MakersBoardRequestDto requestDto) {
        BoardType boardType = BoardType.ofCode(requestDto.getBoardType());
        if(!BoardType.showMakers().contains(boardType)) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        MakersNotice makersNotice = (MakersNotice) backOfficeNoticeRepository.findById(noticeId).orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        makersNotice.setBoardCategory(BoardCategory.getBoardTypeByCategory(BoardType.ofCode(requestDto.getBoardType())));
        backOfficeNoticeMapper.updateNotice(requestDto, makersNotice);
    }

    @Override
    @Transactional
    public void createClientBoard(ClientBoardRequestDto requestDto) {
        BoardType boardType = BoardType.ofCode(requestDto.getBoardType());
        if(!BoardType.showClient().contains(boardType)) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        ClientNotice notice = backOfficeNoticeMapper.toClientNotice(requestDto);
        notice.setBoardCategory(BoardCategory.getBoardTypeByCategory(BoardType.ofCode(requestDto.getBoardType())));
        backOfficeNoticeRepository.save(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<ClientBoardResponseDto> getClientBoard(Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        List<BigInteger> groupIds = !parameters.containsKey("groupIds") || parameters.get("groupIds") == null ? null : StringUtils.parseBigIntegerList((String) parameters.get("groupIds"));
        Boolean isStatus = !parameters.containsKey("isStatus") || parameters.get("isStatus") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isStatus")));
        Boolean isAlarmTalk = !parameters.containsKey("isAlarmTalk") || parameters.get("isAlarmTalk") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isAlarmTalk")));
        BoardType boardType = !parameters.containsKey("boardType") || parameters.get("boardType") == null ? null : BoardType.ofCode(Integer.parseInt(String.valueOf(parameters.get("boardType"))));

        Page<ClientNotice> backOfficeNoticeList = qBackOfficeNoticeRepository.findAllByParameters(groupIds, isStatus, isAlarmTalk, boardType, pageable);

        if(backOfficeNoticeList.isEmpty()) ListItemResponseDto.<ClientBoardResponseDto>builder().items(null).limit(pageable.getPageSize()).offset(pageable.getOffset()).count(0).total((long) backOfficeNoticeList.getTotalPages()).build();

        Map<BigInteger, String> groupNameMap = qGroupRepository.findGroupNameByIds(backOfficeNoticeList.stream().filter(v -> v.getGroupIds() != null).flatMap(v -> v.getGroupIds().stream()).collect(Collectors.toSet()));
        List<ClientBoardResponseDto> appBoardResponseDtos = backOfficeNoticeMapper.toClientBoardResponseDto(backOfficeNoticeList, groupNameMap);

        return ListItemResponseDto.<ClientBoardResponseDto>builder().items(appBoardResponseDtos).limit(pageable.getPageSize()).offset(pageable.getOffset())
                .count(backOfficeNoticeList.getNumberOfElements()).total((long) backOfficeNoticeList.getTotalPages()).isLast(backOfficeNoticeList.isLast()).build();
    }

    @Override
    @Transactional
    public void updateClientBoard(BigInteger noticeId, ClientBoardRequestDto requestDto) {
        BoardType boardType = BoardType.ofCode(requestDto.getBoardType());
        if(!BoardType.showClient().contains(boardType)) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        ClientNotice clientNotice = (ClientNotice) backOfficeNoticeRepository.findById(noticeId).orElseThrow(() -> new ApiException(ExceptionEnum.NOTICE_NOT_FOUND));
        clientNotice.setBoardCategory(BoardCategory.getBoardTypeByCategory(BoardType.ofCode(requestDto.getBoardType())));
        backOfficeNoticeMapper.updateNotice(requestDto, clientNotice);
    }

    @Override
    @Transactional
    public void postAlarmTalk(BigInteger noticeId) throws IOException, ParseException {
        BackOfficeNotice notice = backOfficeNoticeRepository.findByIdAndIsStatus(noticeId, true);
        if(notice == null) throw new ApiException(ExceptionEnum.NOTICE_NOT_FOUND);
        if(notice.getIsAlarmTalk()) throw new ApiException(ExceptionEnum.ALREADY_SEND_ALARM);

        String phone;
        String content;
        List<AlimtalkRequestDto> alimtalkRequestDtoList = new ArrayList<>();

        if(notice instanceof MakersNotice makersNotice) {
            Tuple makersInfo = qMakersRepository.findNameById(makersNotice.getMakersId());
            String name = makersInfo.get(0, String.class);
            phone = makersInfo.get(1, String.class);
            content = kakaoUtil.getContextByMakers(name, notice.getBoardType().getStatus(), selectTemplate(notice.getBoardCategory(), NoticeType.MAKERS));

            alimtalkRequestDtoList.add(new AlimtalkRequestDto(phone, null, content));
        }

        else if (notice instanceof ClientNotice clientNotice) {
            Map<String, BigInteger> clientNameList = qGroupRepository.findGroupNameListByIds(clientNotice.getGroupIds());
            Map<BigInteger, String> managerMap = qUserRepository.findUserIdAndPhoneByUserId((List<BigInteger>) clientNameList.values());
            for (String name : clientNameList.keySet()) {
                phone = managerMap.entrySet().stream()
                        .filter(entry -> clientNameList.get(name).equals(entry.getKey()))
                        .findAny().orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "CE400027", name + "의 매니저 정보가 없습니다. 확인해주세요."))
                        .getValue();

                content = kakaoUtil.getContextByClient(name, notice.getBoardType().getStatus(), selectTemplate(notice.getBoardCategory(), NoticeType.CLIENT));
                alimtalkRequestDtoList.add(new AlimtalkRequestDto(phone, null, content));
            }
        }

        pushService.sendToTalk(alimtalkRequestDtoList);
    }

    private AlimTalkTemplate selectTemplate(BoardCategory boardCategory, NoticeType noticeType) {
        Map<BoardCategory, Map<NoticeType, AlimTalkTemplate>> templateMap = new HashMap<>();
        Map<NoticeType, AlimTalkTemplate> allTemplates = new HashMap<>();
        allTemplates.put(NoticeType.MAKERS, AlimTalkTemplate.ALL_MAKERS);
        allTemplates.put(NoticeType.CLIENT, AlimTalkTemplate.ALL_CLIENT);

        Map<NoticeType, AlimTalkTemplate> paycheckOrEventTemplates = new HashMap<>();
        paycheckOrEventTemplates.put(NoticeType.MAKERS, AlimTalkTemplate.INDIVIDUAL_MAKERS);
        paycheckOrEventTemplates.put(NoticeType.CLIENT, AlimTalkTemplate.INDIVIDUAL_CLIENT);

        Map<NoticeType, AlimTalkTemplate> approveChangeTemplates = new HashMap<>();
        approveChangeTemplates.put(NoticeType.MAKERS, AlimTalkTemplate.APPROVE_MAKERS);
        approveChangeTemplates.put(NoticeType.CLIENT, AlimTalkTemplate.APPROVE_CLIENT);

        templateMap.put(BoardCategory.ALL, allTemplates);
        templateMap.put(BoardCategory.PAYCHECK, paycheckOrEventTemplates);
        templateMap.put(BoardCategory.EVENT, paycheckOrEventTemplates);
        templateMap.put(BoardCategory.APPROVE_CHANGE, approveChangeTemplates);

        return templateMap.get(boardCategory).get(noticeType);
    }

}