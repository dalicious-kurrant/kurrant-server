package co.dalicious.domain.address.utils;

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

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application-map.properties")
public class AddressUtil {

    @Value("${naver.client.id}")
    public String YOUR_CLIENT_ID;

    @Value("${naver.secret.id}")
    public String YOUR_CLIENT_SECRET;

    public String getLocation (String address) {

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

}
