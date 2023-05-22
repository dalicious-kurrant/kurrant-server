package co.kurrant.app.admin_api.service;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.HandlePushAlarmDto;
import co.kurrant.app.admin_api.dto.alimtalk.AlimtalkTestDto;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface PushAlarmService {
    List<AutoPushAlarmDto.AutoPushAlarmList> findAllAutoPushAlarmList();
    void updateAutoPushAlarmMessage(AutoPushAlarmDto.AutoPushAlarmMessageReqDto reqDto);
    void updateAutoPushAlarmStatus(AutoPushAlarmDto.AutoPushAlarmStatusReqDto reqDto);
    void updateAutoPushAlarmUrl(AutoPushAlarmDto.AutoPushAlarmUrlReqDto reqDto);
    List<HandlePushAlarmDto.HandlePushAlarmType> findAllTypeList();
    List<HandlePushAlarmDto.HandlePushAlarm> findAllListByType(Integer type);
    void createHandlePushAlarmList(List<HandlePushAlarmDto.HandlePushAlarmReqDto> reqDtoList);

    void alimtalkSendTest(AlimtalkTestDto alimtalkTestDto) throws IOException, ParseException;
}
