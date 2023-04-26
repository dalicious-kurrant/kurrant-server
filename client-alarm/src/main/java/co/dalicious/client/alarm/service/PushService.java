package co.dalicious.client.alarm.service;

import co.dalicious.client.alarm.dto.AlimtalkRequestDto;
import co.dalicious.client.alarm.dto.PushByTopicRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.dto.PushTokenSaveReqDto;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface PushService {
    void sendToPush(PushRequestDto pushRequestDto);

    void sendByTopic(PushByTopicRequestDto pushByTopicRequestDto);

    void sendToTalk(AlimtalkRequestDto alimtalkRequestDto) throws IOException, ParseException;

    void saveToken(PushTokenSaveReqDto pushTokenSaveReqDto);
}
