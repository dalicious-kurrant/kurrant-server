package co.dalicious.client.alarm.mapper;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.entity.PushAlarms;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface PushAlarmMapper {

    default AutoPushAlarmDto.AutoPushAlarmList toAutoPushAlarmListDto(PushAlarms pushAlarms) {
        AutoPushAlarmDto.AutoPushAlarmList autoPushAlarmList = new AutoPushAlarmDto.AutoPushAlarmList();

        autoPushAlarmList.setStatus(pushAlarms.getPushStatus().getCode());
        autoPushAlarmList.setId(pushAlarms.getId());
        autoPushAlarmList.setCondition(pushAlarms.getCondition().getCondition());
        autoPushAlarmList.setMessage(pushAlarms.getMessage());
        autoPushAlarmList.setUrl(pushAlarms.getRedirectUrl());

        return autoPushAlarmList;
    }

    default PushRequestDto toPushRequestDto(List<String> firebaseToken, String title , String message, String page, Map<String, String> keys) {
        PushRequestDto pushRequestDto = new PushRequestDto();

        pushRequestDto.setTokenList(firebaseToken);
        pushRequestDto.setTitle(title);
        pushRequestDto.setContent(message);
        pushRequestDto.setPage(page);
        pushRequestDto.setKeys(keys);

        return pushRequestDto;
    }
}
