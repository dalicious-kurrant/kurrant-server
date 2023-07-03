package co.dalicious.client.alarm.mapper;

import co.dalicious.client.alarm.dto.AutoPushAlarmDto;
import co.dalicious.client.alarm.dto.BatchAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.PushAlarms;

import co.dalicious.domain.user.entity.BatchPushAlarmLog;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import org.mapstruct.Mapper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    default PushRequestDto toPushRequestDto(List<String> tokenList, String title, String message, String page, Map<String, String> keys) {
        PushRequestDto pushRequestDto = new PushRequestDto();
        pushRequestDto.setTokenList(tokenList);
        pushRequestDto.setTitle(title);
        pushRequestDto.setMessage(message);
        pushRequestDto.setPage(page);
        pushRequestDto.setKeys(keys);

        return pushRequestDto;
    }

    default PushRequestDtoByUser toPushRequestDtoByUser(String token, String title, String message, String page) {
        PushRequestDtoByUser pushRequestDto = new PushRequestDtoByUser();
        pushRequestDto.setToken(token);
        pushRequestDto.setTitle(title);
        pushRequestDto.setMessage(message);
        pushRequestDto.setPage(page);

        return pushRequestDto;
    }

    default BatchPushAlarmLog toBatchPushAlarmLog(BigInteger id, PushCondition pushCondition, LocalDateTime logDatetime) {
        return BatchPushAlarmLog.builder()
                .userId(id)
                .pushDateTime(logDatetime)
                .pushCondition(pushCondition)
                .build();
    }

    default BatchAlarmDto toBatchAlarmDto(PushRequestDtoByUser dto, User user) {

        Map<String, BigInteger> tokenList = new HashMap<>();
        tokenList.put(dto.getToken(), user.getId());

        BatchAlarmDto batchAlarmDto = new BatchAlarmDto();
        batchAlarmDto.setMessage(dto.getMessage());
        batchAlarmDto.setTitle(dto.getTitle());
        batchAlarmDto.setPage(dto.getPage());
        batchAlarmDto.setTokenList(tokenList);

        return batchAlarmDto;
    }
}
