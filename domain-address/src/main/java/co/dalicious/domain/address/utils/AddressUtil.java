package co.dalicious.domain.address.utils;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application-map.properties")
public class AddressUtil {

    @Value("${naver.client.id}")
    public static String YOUR_CLIENT_ID;

    @Value("${naver.secret.id}")
    public static String YOUR_CLIENT_SECRET;

    public static String getLocation(String address) {

        String locationResult = null;
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + encodedAddress;
            // HTTP 요청 보내기
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", YOUR_CLIENT_ID);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", YOUR_CLIENT_SECRET);
            conn.connect();

            int responseCode = conn.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONTokener tokener = new JSONTokener(response.toString());
            JSONObject object = new JSONObject(tokener);
            JSONArray arr = object.getJSONArray("addresses");

            if(arr.length() < 1) throw new Exception(address + ", 주소를 확인해주세요.");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                System.out.println("address : " + temp.get("roadAddress"));
                System.out.println("jibunAddress : " + temp.get("jibunAddress"));
                String latitude = String.valueOf(temp.get("y"));
                String longitude = String.valueOf(temp.get("x"));

                locationResult = latitude + " " + longitude;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return locationResult;
    }

    public static String getAddress3(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        String regex = "([가-힣A-Za-z·\\d~\\-\\.]+(시|군|구)\\s)[\\d]+";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);

        if (matcher.find()) {
            String result = matcher.group();
            System.out.println(result);
        }
        return null;
    }

}
