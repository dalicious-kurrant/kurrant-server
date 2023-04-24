package co.dalicious.client.alarm.service.impl;

import co.dalicious.client.alarm.dto.AlimtalkRequestDto;
import co.dalicious.client.alarm.dto.PushByTopicRequestDto;
import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.dto.PushTokenSaveReqDto;
import co.dalicious.client.alarm.util.KakaoUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.client.alarm.service.PushService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {

    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final QUserRepository qUserRepository;

    @Override
    public void sendToPush(PushRequestDto pushRequestDto) {

        List<String> tokenList = pushRequestDto.getTokenList();
        String title = pushRequestDto.getTitle();
        String content = pushRequestDto.getContent();
        String page = pushRequestDto.getPage();
        Map<String, String> keys = pushRequestDto.getKeys();

        List<Message> messages;

        // 관련 파라미터가 있으면
        if(keys != null && !keys.isEmpty()) {
            messages = tokenList.stream().map(token -> Message.builder()
                    .putData("time", LocalDateTime.now().toString())
                    .putData("page", page)
                    .putAllData(keys)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .setToken(token)
                    .build()).collect(Collectors.toList());
        }
        // 없으면
        else {
            messages = tokenList.stream().map(token -> Message.builder()
                    .putData("time", LocalDateTime.now().toString())
                    .putData("page", page)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .setToken(token)
                    .build()).collect(Collectors.toList());
        }

        if(messages.isEmpty()) return;

        //알림 발송
        BatchResponse response;
        try {

            response = FirebaseMessaging.getInstance(FirebaseApp.getInstance("dalicious-v1")).sendAll(messages);

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

    @Override
    @Transactional
    public void saveToken(PushTokenSaveReqDto pushTokenSaveReqDto) {
        //유저ID로 유저 정보 가져오기
        User user = userRepository.findById(pushTokenSaveReqDto.getUserId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));

        long result = qUserRepository.saveFcmToken(pushTokenSaveReqDto.getToken(), user.getId());
        if (result != 1){
            throw new ApiException(ExceptionEnum.TOKEN_SAVE_FAILED);
        }
    }
}
