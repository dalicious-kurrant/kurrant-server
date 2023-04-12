package co.kurrant.app.admin_api.service;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.HandlePushAlarmDto;

import java.util.List;

public interface PushAlarmService {
    List<AutoPushAlarmDto.AutoPushAlarmList> findAllAutoPushAlarmList();
    void updateAutoPushAlarmMessage(AutoPushAlarmDto.AutoPushAlarmMessageReqDto reqDto);
    void updateAutoPushAlarmStatus(AutoPushAlarmDto.AutoPushAlarmStatusReqDto reqDto);
    void updateAutoPushAlarmUrl(AutoPushAlarmDto.AutoPushAlarmUrlReqDto reqDto);
    List<HandlePushAlarmDto.HandlePushAlarmType> findAllTypeList();
    List<HandlePushAlarmDto.HandlePushAlarmGroup> findAllGroupList(Integer type);
    List<HandlePushAlarmDto.HandlePushAlarmSpot> findAllSpotList(Integer type);
    List<HandlePushAlarmDto.HandlePushAlarmUser> findAllUserList(Integer type);
    void createHandlePushAlarmList(List<HandlePushAlarmDto.HandlePushAlarmReqDto> reqDtoList);
}
