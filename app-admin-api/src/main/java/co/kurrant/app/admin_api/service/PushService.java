package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.push.AlimtalkRequestDto;
import co.kurrant.app.admin_api.dto.push.PushByTopicRequestDto;
import co.kurrant.app.admin_api.dto.push.PushRequestDto;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface PushService {
    void sendToPush(PushRequestDto pushRequestDto);

    void sendByTopic(PushByTopicRequestDto pushByTopicRequestDto);

    void sendToTalk(AlimtalkRequestDto alimtalkRequestDto) throws IOException, ParseException;
}
