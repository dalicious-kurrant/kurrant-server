package co.kurrant.app.admin_api.service.impl;

import co.dalicious.system.util.KakaoUtil;
import co.kurrant.app.admin_api.dto.push.AlimtalkRequestDto;
import co.kurrant.app.admin_api.dto.push.PushByTopicRequestDto;
import co.kurrant.app.admin_api.dto.push.PushRequestDto;
import co.kurrant.app.admin_api.service.PushService;
import com.google.firebase.messaging.*;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {

    private final KakaoUtil kakaoUtil;

    @Override
    public void sendToPush(PushRequestDto pushRequestDto) {

        List<String> tokenList = pushRequestDto.getTokenList();
        String title = pushRequestDto.getTitle();
        String content = pushRequestDto.getContent();

        List<Message> messages = tokenList.stream().map(token -> Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .setToken(token)
                .build()).collect(Collectors.toList());

        //알림 발송
        BatchResponse response;
        try {

            response = FirebaseMessaging.getInstance().sendAll(messages);

            //응답처리
            if(response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for(int i = 0; i < responses.size(); i++) {
                    if(!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                System.out.println("List of tokens are not valid FCM token : " + failedTokens);
            }
        } catch(FirebaseMessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void sendByTopic(PushByTopicRequestDto pushByTopicRequestDto) {

        String title = pushByTopicRequestDto.getTitle();
        String content = pushByTopicRequestDto.getContent();

        String topic = null;
        switch(pushByTopicRequestDto.getTopic().toLowerCase()) {
            case ("push"):
                topic = "appPush";
                break;
            case ("event"):
                topic = "event";
                break;
            case ("notice") :
                topic = "notice";
                break;
            default :
                throw new ApiException(ExceptionEnum.BAD_REQUEST_TOPIC);
        }

        Message message = Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .setTopic(topic)
                .build();

        String response;
        try {
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println("push를 성공적으로 보냈습니다.");
        } catch (FirebaseMessagingException e) {
            System.out.println("cannot send to memberList push message. error info : ()" + e.getMessage());
        }

    }

    @Override
    public void sendToTalk(AlimtalkRequestDto alimtalkRequestDto) throws IOException, ParseException {
        //String content = "(테이스팅 날짜 확정 안내)\n\n안녕하세요. 커런트입니다.\n\n요청하신 신규메뉴 테이스팅에 대한 일정이 확정 되었습니다.\n\n확인 부탁 드립니다.\n\n감사합니다.\n\n▶메이커스 이름 : 민지네식탁\n\n▶테이스팅 날짜 : 2023-03-16 \n\n▶www.naver.com";
        JSONObject jsonObject = kakaoUtil.sendAlimTalk(alimtalkRequestDto.getPhoneNumber(), alimtalkRequestDto.getContent(), alimtalkRequestDto.getTemplateId());
        long code = (long) jsonObject.get("code");
        if (code != 0){
            System.out.println(jsonObject +"result");
            throw new ApiException(ExceptionEnum.ALIMTALK_SEND_FAILED);
        }
        System.out.println(jsonObject + "jsonResult");

    }
}
