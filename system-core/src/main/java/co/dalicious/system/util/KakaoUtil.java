package co.dalicious.system.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class KakaoUtil {

    public JSONObject sendAlimTalk(String phoneNumber, String content, String templateId) throws IOException, ParseException {
        URL url = new URL("https://jupiter.lunasoft.co.kr/api/AlimTalk/message/send");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        JSONArray jsonArray = new JSONArray();
        Map<String,Object> messages = new HashMap<>();
        messages.put("no", "1");
        messages.put("tel_num", phoneNumber);
        messages.put("msg_content", content);
        messages.put("sms_content", content);
        messages.put("use_sms", "1");
        jsonArray.add(messages);
        System.out.println(jsonArray + " jsonArray");

        JSONObject obj = new JSONObject();
        obj.put("userid", "dalicious");
        obj.put("api_key", "vdbmow6r5beyyyv4d58mb0fxaid8eh2uiqzpains");
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

}
