package co.dalicious.domain.payment.util;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

@Component
public class TossUtil {

    private String secretKey;

    TossUtil(@Value("${toss.secret-key}") String secretKey){
        this.secretKey = secretKey;
    }
    public String createCustomerKey() {
        int leftLimit = 48; // 숫자 '0'
        int rightLimit = 122; // 영소문자 'z'
        int targetStringLength = 50; // 길이제한
        Random random = new Random();

        String generatedString = random.ints(leftLimit,rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public HttpRequest cardRegisterRequest(String cardNumber, String expirationYear, String expirationMonth,
                          String cardPassword, String identityNumber, String customerKey){
        byte[] secretKeyToByte = secretKey.getBytes();

        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKeyToByte);

        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/card"))
                .header("Authorization", "Basic " +new String(encodeByte))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cardNumber\":\""+cardNumber+"\",\"cardExpirationYear\":\""+expirationYear+"\",\"cardExpirationMonth\":\""+expirationMonth+"\",\"cardPassword\":\""+cardPassword+"\",\"customerIdentityNumber\":\""+identityNumber+"\",\"customerKey\":\""+customerKey+"\"}"))
                .build();
    }

    public JSONObject payToCard(String customerKey, Integer amount,String orderId, String orderName, String billingKey) throws IOException, InterruptedException, ParseException {

        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic "+ new String(encodeByte, 0, encodeByte.length);

        URL url = new URL("https://api.tosspayments.com/v1/billing/" + billingKey);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("customerKey", customerKey);
        obj.put("amount", amount);
        obj.put("orderId", orderId);
        obj.put("orderName", orderName);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200? true : false;

        InputStream responseStream = isSuccess? connection.getInputStream(): connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        return jsonObject;


//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.tosspayments.com/v1/billing/"+billingKey))
//                .header("Authorization", "Basic "+new String(encodeByte))
//                .header("Content-Type", "application/json")
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\"customerKey\":\""+customerKey+"\",\"amount\":"+amount+",\"orderId\":\""+orderId+"\",\"orderName\":\""+orderName+"\"}"))
//                .build();
//       return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

}
