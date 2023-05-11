package co.dalicious.client.alarm.service;

import co.dalicious.client.alarm.dto.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface PushService {
    void sendToPush(PushRequestDto pushRequestDto);
    void sendToPush(List<PushRequestDtoByUser> pushRequestDtoByUsers);

    void sendByTopic(PushByTopicRequestDto pushByTopicRequestDto);

    void sendToTalk(AlimtalkRequestDto alimtalkRequestDto) throws IOException, ParseException;

    void saveToken(PushTokenSaveReqDto pushTokenSaveReqDto);
}
