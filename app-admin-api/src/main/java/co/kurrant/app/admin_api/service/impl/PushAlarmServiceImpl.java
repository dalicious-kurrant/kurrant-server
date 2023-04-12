package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.HandlePushAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.entity.PushAlarms;
import co.dalicious.client.alarm.entity.enums.PushStatus;
import co.dalicious.client.alarm.service.PushService;
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
import co.kurrant.app.admin_api.mapper.PushAlarmTypeMapper;
import co.kurrant.app.admin_api.service.PushAlarmService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    @Transactional(readOnly = true)
    public List<AutoPushAlarmDto.AutoPushAlarmList> findAllAutoPushAlarmList() {
        List<PushAlarms> pushAlarmsList = pushAlarmRepository.findAll();

        List<AutoPushAlarmDto.AutoPushAlarmList> pushAlarmListDtoList = new ArrayList<>();
        if(pushAlarmsList.isEmpty()) return pushAlarmListDtoList;

        for(PushAlarms pushAlarms : pushAlarmsList) {
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
    public List<HandlePushAlarmDto.HandlePushAlarmGroup> findAllGroupList(Integer type) {
        List<HandlePushAlarmDto.HandlePushAlarmGroup> handlePushAlarmGroupList = new ArrayList<>();
        if(HandlePushAlarmType.GROUP.equals(HandlePushAlarmType.ofCode(type))) {
            List<Group> groupList = groupRepository.findAll();
            handlePushAlarmGroupList = groupList.stream().map(pushAlarmTypeMapper::toHandlePushAlarmGroup).collect(Collectors.toList());
        }
        return handlePushAlarmGroupList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HandlePushAlarmDto.HandlePushAlarmSpot> findAllSpotList(Integer type) {
        List<HandlePushAlarmDto.HandlePushAlarmSpot> handlePushAlarmSpotList = new ArrayList<>();
        if(HandlePushAlarmType.SPOT.equals(HandlePushAlarmType.ofCode(type))) {
            List<Spot> spotList = spotRepository.findAll();
            handlePushAlarmSpotList = spotList.stream().map(pushAlarmTypeMapper::toHandlePushAlarmSpot).collect(Collectors.toList());
        }
        return handlePushAlarmSpotList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HandlePushAlarmDto.HandlePushAlarmUser> findAllUserList(Integer type) {
        List<HandlePushAlarmDto.HandlePushAlarmUser> handlePushAlarmUserList = new ArrayList<>();
        if(HandlePushAlarmType.USER.equals(HandlePushAlarmType.ofCode(type))) {
            List<User> userList = userRepository.findAll();
            handlePushAlarmUserList = userList.stream().map(pushAlarmTypeMapper::toHandlePushAlarmUser).collect(Collectors.toList());
        }
        return handlePushAlarmUserList;
    }

    @Override
    @Transactional
    public void createHandlePushAlarmList(List<HandlePushAlarmDto.HandlePushAlarmReqDto> reqDtoList) {
        List<PushRequestDto> pushRequestDtoList = new ArrayList<>();

        for(HandlePushAlarmDto.HandlePushAlarmReqDto reqDto : reqDtoList) {
            if(HandlePushAlarmType.ALL.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> allUserFcmToken = qUserRepository.findAllUserFirebaseToken();
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(allUserFcmToken, null, reqDto.getMessage(), reqDto.getPage());
                pushRequestDtoList.add(pushRequestDto);
            }
            else if(HandlePushAlarmType.GROUP.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> allUserGroupFcmToken = qUserGroupRepository.findUserGroupFirebaseToken(reqDto.getGroupIds());
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(allUserGroupFcmToken, null, reqDto.getMessage(), reqDto.getPage());
                pushRequestDtoList.add(pushRequestDto);
            }
            else if(HandlePushAlarmType.SPOT.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> allUserSpotFcmToken = qUserSpotRepository.findAllUserSpotFirebaseToken(reqDto.getSpotIds());
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(allUserSpotFcmToken, null, reqDto.getMessage(), reqDto.getPage());
                pushRequestDtoList.add(pushRequestDto);
            }
            else if(HandlePushAlarmType.USER.equals(HandlePushAlarmType.ofCode(reqDto.getType()))) {
                List<String> userFirebaseToken = qUserRepository.findUserFirebaseToken(reqDto.getUserIds());
                PushRequestDto pushRequestDto = pushAlarmMapper.toPushRequestDto(userFirebaseToken, null, reqDto.getMessage(), reqDto.getPage());
                pushRequestDtoList.add(pushRequestDto);
            }
        }

        pushRequestDtoList.forEach(pushService::sendToPush);
    }
}
