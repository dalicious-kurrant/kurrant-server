package co.dalicious.domain.application_form.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicationSlackUtil {
    public void sendSlack(String message){
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();
        request.put("username", "Spot 신청 알리미"); //slack bot name
        request.put("text", message); //전송할 메세지
        request.put("icon_emoji", ":slack:"); //slack bot image

        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);

        String url = "https://hooks.slack.com/services/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"; //복사한 Webhook URL 입력

        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    }

}
