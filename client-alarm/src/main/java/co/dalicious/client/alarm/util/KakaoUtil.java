package co.dalicious.client.alarm.util;

import co.dalicious.client.alarm.entity.enums.AlimTalkTemplate;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource("classpath:application-alimtalk.properties")
public class KakaoUtil {

    private String apiKey;

    KakaoUtil(@Value("${lunasoft.api-key}")String apiKey){
        this.apiKey = apiKey;
    }

    public JSONObject sendAlimTalk(String phoneNumber, String content, String templateId, String redirectUrl) throws IOException, ParseException {
        URL url = new URL("https://jupiter.lunasoft.co.kr/api/AlimTalk/message/send");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        JSONArray jsonArray = new JSONArray();
        JSONArray link = new JSONArray();

        Map<String,Object> linkMap = new HashMap<>();
        linkMap.put("url_pc", redirectUrl);
        linkMap.put("url_mobile", redirectUrl);
        link.add(linkMap);

        Map<String,Object> messages = new HashMap<>();
        messages.put("no", "0");
        messages.put("tel_num", phoneNumber);
        messages.put("msg_content", content);
        messages.put("sms_content", content);
        messages.put("use_sms", "1");
        messages.put("btn_url", link);
        jsonArray.add(messages);
        System.out.println(jsonArray + " jsonArray");

        JSONObject obj = new JSONObject();
        obj.put("userid", "dalicious");
        obj.put("api_key", apiKey);
        obj.put("template_id", templateId);
        obj.put("messages", jsonArray);
        System.out.println(obj.toJSONString());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200? true : false;

        InputStream responseStream = isSuccess? connection.getInputStream(): connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        return jsonObject;
    }

    public String getContextByMakers(String name, String type, AlimTalkTemplate alimTalkTemplate) {
        String template = alimTalkTemplate.getTemplate();
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("makersName", name);
        valuesMap.put("BoardType", type);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }

    public String getContextByClient(String name, String type, AlimTalkTemplate alimTalkTemplate) {
        String template = alimTalkTemplate.getTemplate();
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("clientName", name);
        valuesMap.put("BoardType", type);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        template = sub.replace(template);
        return template;
    }
}
