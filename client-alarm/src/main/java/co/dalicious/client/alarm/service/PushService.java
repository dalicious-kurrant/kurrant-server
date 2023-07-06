package co.dalicious.client.alarm.service;

import co.dalicious.client.alarm.dto.*;
import co.dalicious.domain.user.entity.enums.PushCondition;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PushService {
    void sendToPushByKey(List<PushRequestDtoByUser> pushRequestDtoByUsers, Map<String, String> keys);
    void sendToPush(List<PushRequestDtoByUser> pushRequestDtoByUsers);
    void sendToPush(BatchAlarmDto batchAlarmDto, PushCondition pushCondition);

    void sendByTopic(PushByTopicRequestDto pushByTopicRequestDto);

    void sendToTalk(AlimtalkRequestDto alimtalkRequestDto) throws IOException, ParseException;

    void saveToken(PushTokenSaveReqDto pushTokenSaveReqDto);
}
