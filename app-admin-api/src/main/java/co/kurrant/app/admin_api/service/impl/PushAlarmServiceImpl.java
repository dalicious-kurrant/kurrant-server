package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.HandlePushAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.entity.enums.PushStatus;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.KakaoUtil;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.client.alarm.mapper.PushAlarmMapper;
import co.dalicious.client.alarm.repository.PushAlarmRepository;
import co.dalicious.client.alarm.entity.enums.HandlePushAlarmType;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.GroupRepository;
import co.kurrant.app.admin_api.dto.alimtalk.AlimtalkTestDto;
import co.kurrant.app.admin_api.mapper.PushAlarmTypeMapper;
import co.kurrant.app.admin_api.service.PushAlarmService;
import com.querydsl.core.Tuple;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PushAlarmServiceImpl implements PushAlarmService {

    private final PushAlarmRepository pushAlarmRepository;
    private final PushAlarmMapper pushAlarmMapper;
    private final GroupRepository groupRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;
    private final QUserRepository qUserRepository;
    private final PushAlarmTypeMapper pushAlarmTypeMapper;
    private final QUserGroupRepository qUserGroupRepository;
    private final QUserSpotRepository qUserSpotRepository;
    private final PushService pushService;
    private final KakaoUtil kakaoUtil;
    private final PushUtil pushUtil;

    @Override
    @Transactional(readOnly = true)
    public List<AutoPushAlarmDto.AutoPushAlarmList> findAllAutoPushAlarmList() {
        List<PushAlarms> pushAlarmsList = pushAlarmRepository.findAll();

        List<AutoPushAlarmDto.AutoPushAlarmList> pushAlarmListDtoList = new ArrayList<>();
        if (pushAlarmsList.isEmpty()) return pushAlarmListDtoList;

        for (PushAlarms pushAlarms : pushAlarmsList) {
            AutoPushAlarmDto.AutoPushAlarmList pushAlarmListDto = pushAlarmMapper.toAutoPushAlarmListDto(pushAlarms);
            pushAlarmListDtoList.add(pushAlarmListDto);
        }

        return pushAlarmListDtoList;
    }

    @Override
    @Transactional
    public void updateAutoPushAlarmMessage(AutoPushAlarmDto.AutoPushAlarmMessageReqDto reqDto) {
        PushAlarms pushAlarms = pushAlarmRepository.findById(reqDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_PUSH_ALARM));

        pushAlarms.updateMessage(reqDto.getMessage());
    }

    @Override
    @Transactional
    public void updateAutoPushAlarmStatus(AutoPushAlarmDto.AutoPushAlarmStatusReqDto reqDto) {
        PushAlarms pushAlarms = pushAlarmRepository.findById(reqDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_PUSH_ALARM));

        pushAlarms.updatePushStatus(PushStatus.ofCode(reqDto.getStatus()));
    }

    @Override
    @Transactional
    public void updateAutoPushAlarmUrl(AutoPushAlarmDto.AutoPushAlarmUrlReqDto reqDto) {
        PushAlarms pushAlarms = pushAlarmRepository.findById(reqDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_PUSH_ALARM));

        pushAlarms.updateRedirectUrl(reqDto.getUrl());
    }

    @Override
    public List<HandlePushAlarmDto.HandlePushAlarmType> findAllTypeList() {
        List<HandlePushAlarmType> pushAlarmTypeList = List.of(HandlePushAlarmType.class.getEnumConstants());
        return pushAlarmTypeList.stream().map(pushAlarmTypeMapper::toHandlePushAlarmType).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HandlePushAlarmDto.HandlePushAlarm> findAllListByType(Integer type) {
        List<HandlePushAlarmDto.HandlePushAlarm> handlePushAlarmByTypeList = new ArrayList<>();
        if (HandlePushAlarmType.GROUP.equals(HandlePushAlarmType.ofCode(type))) {
            List<Group> groupList = groupRepository.findAll();
            handlePushAlarmByTypeList = groupList.stream().map(g -> pushAlarmTypeMapper.toHandlePushAlarmByType(g, null, null)).collect(Collectors.toList());
        } else if (HandlePushAlarmType.SPOT.equals(HandlePushAlarmType.ofCode(type))) {
            List<Spot> spotList = spotRepository.findAll();
            handlePushAlarmByTypeList = spotList.stream().map(s -> pushAlarmTypeMapper.toHandlePushAlarmByType(null, s, null)).collect(Collectors.toList());
        } else if (HandlePushAlarmType.USER.equals(HandlePushAlarmType.ofCode(type))) {
            List<User> userList = userRepository.findAll();
            handlePushAlarmByTypeList = userList.stream().map(u -> pushAlarmTypeMapper.toHandlePushAlarmByType(null, null, u)).collect(Collectors.toList());
        }

        return handlePushAlarmByTypeList;
    }

    @Override
    @Transactional
    public void createHandlePushAlarmList(List<HandlePushAlarmDto.HandlePushAlarmReqDto> reqDtoList) {
        List<PushRequestDto> pushRequestDtoList = new ArrayList<>();
        // TODO: 유저마다 다른 타입의 메세지를 보낼 경우 수정 필요
        for (HandlePushAlarmDto.HandlePushAlarmReqDto reqDto : reqDtoList) {
            if (HandlePushAlarmType.ALL.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<Tuple> allUserWithFcmToken = qUserRepository.findAllUserFirebaseToken();
                List<String> allUserFcmToken = allUserWithFcmToken.stream().map(tuple -> tuple.get(0)).toList();
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(allUserFcmToken, null, reqDto.getMessage(), reqDto.getPage(), null);
                pushRequestDtoList.add(pushRequestDto);
            } else if (HandlePushAlarmType.GROUP.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> allUserGroupFcmToken = qUserGroupRepository.findUserGroupFirebaseToken(reqDto.getGroupIds());
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(allUserGroupFcmToken, null, reqDto.getMessage(), reqDto.getPage(), null);
                pushRequestDtoList.add(pushRequestDto);
            } else if (HandlePushAlarmType.SPOT.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> allUserSpotFcmToken = qUserSpotRepository.findAllUserSpotFirebaseToken(reqDto.getSpotIds());
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(allUserSpotFcmToken, null, reqDto.getMessage(), reqDto.getPage(), null);
                pushRequestDtoList.add(pushRequestDto);
            } else if (HandlePushAlarmType.USER.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> userFirebaseToken = qUserRepository.findUserFirebaseToken(reqDto.getUserIds());
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(userFirebaseToken, null, reqDto.getMessage(), reqDto.getPage(), null);
                pushRequestDtoList.add(pushRequestDto);
            }
        }

        pushRequestDtoList.forEach(v -> {
            pushUtil.savePushAlarmHash(v.getTitle(), v.getMessage(), );
            pushService::sendToPush
        });
    }

    @Override
    public void alimtalkSendTest(AlimtalkTestDto alimtalkTestDto) throws IOException, ParseException {

        LocalDate localDate = LocalDate.now();
        String date = localDate.format(DateTimeFormatter.ofPattern("yyyy.M.dd"));

        //50063
        String content = "(정산신청 완료)\n\n안녕하세요. 커런트입니다. \n\n정산신청이 완료되었습니다.\n\n▶입금예정일: 2023.5.12";

        //50065
        String makersName = "알렉산더";
        String content65 = "(입금 완료 안내) \n안녕하세요. 커런트입니다. \n"+makersName+"메이커스의 "+2023+"년 "+5+"월 정산금이 이체되었습니다. \n▶정산금액 : "+5000+"";

        //50074
        String content2 = "안녕하세요!\n" +
                "조식 서비스를 운영 중인 커런트입니다.\n" +
                "\n" +
                "회원 가입 시, 동호수가 입력되지 않았습니다.\n" +
                "커런트 어플 내 왼쪽 상단바 (실선 3개) - 개인정보 - 이름(동호수) 정보 변경 부탁드립니다.\n" +
                "\n" +
                "동호수 미기입 시에는 배송이 누락될 수 있습니다.\n" +
                "\n" +
                "감사합니다.";

        kakaoUtil.sendAlimTalk(alimtalkTestDto.getPhoneNumber(), content65, alimtalkTestDto.getTemplateId());


    }
}
