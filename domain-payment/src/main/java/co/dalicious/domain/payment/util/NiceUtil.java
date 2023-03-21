package co.dalicious.domain.payment.util;

import exception.ApiException;
import exception.ExceptionEnum;
import io.swagger.annotations.Api;
import io.swagger.v3.core.util.Json;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

@Component
@PropertySource("classpath:application-payment.properties")
public class NiceUtil {

    private String secretKey;
    private String apiKey;

    NiceUtil(@Value("${nice.api-key}") String apiKey,
             @Value("${nice.secret-key}") String secretKey
             ){
        this.secretKey = secretKey;
        this.apiKey = apiKey;
    }


    public String getToken() throws IOException, ParseException {
        URL url = new URL("https://api.iamport.kr/users/getToken");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("imp_key", apiKey);
        obj.put("imp_secret", secretKey);


        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        Long resultCode = (Long) jsonObject.get("code");
        if (resultCode != 0 ){
            throw new ApiException(ExceptionEnum.TOKEN_CREATE_FAILED);
        }
        JSONObject response = (JSONObject) jsonObject.get("response");
        Object access_token = response.get("access_token");
        return (String) access_token;
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

    //카드 등록요청(자동결제 빌링키 발급)
    public JSONObject cardRegisterRequest(String cardNumber, String expirationYear, String expirationMonth,
                                    String cardPassword, String identityNumber, String customerKey, String token) throws IOException, ParseException {
        byte[] secretKeyToByte = secretKey.getBytes();

        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKeyToByte);
        String authorizations = "Basic " + new String(encodeByte, 0, encodeByte.length);

        URL url = new URL("https://api.iamport.kr/subscribe/customers/"+customerKey);

        //유효기간 년월 합치기
        String expiry = "20" + expirationYear +"-" +expirationMonth;

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("pg", "nice");
        obj.put("card_number", cardNumber);
        obj.put("expiry", expiry);
        obj.put("pwd_2digit", cardPassword);
        obj.put("birth", identityNumber);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        System.out.println(jsonObject + "jsonObject");
        Long resultCode = (Long) jsonObject.get("code");
        if (resultCode != 0){
            throw new ApiException(ExceptionEnum.BILLING_KEY_CREATE_FAILED);
        }
        responseStream.close();
        JSONObject response = (JSONObject) jsonObject.get("response");
        return response;
    }

    public JSONObject niceBilling(String billingKey, Integer amount, String orderId, String token, String orderName) throws IOException, ParseException {
        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic "+ new String(encodeByte, 0, encodeByte.length);

        URL url = new URL("https://api.iamport.kr/subscribe/payments/again");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("customer_uid", billingKey);
        obj.put("amount", amount);
        obj.put("merchant_uid", orderId);
        obj.put("name", orderName);

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
    }

    //결제 취소
    public JSONObject cardCancelOne(String impUid, String cancelReason, int amount, String token) throws IOException, ParseException {
        URL url = new URL("https://api.iamport.kr/payments/cancel");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("imp_uid", impUid);
        obj.put("amount", amount);
        obj.put("reason", cancelReason);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        System.out.println(jsonObject + "jsonObject");
        Long resultCode = (Long) jsonObject.get("code");
        if (resultCode != 0){
            throw new ApiException(ExceptionEnum.PAYMENT_CANCELLATION_FAILED);
        }
        responseStream.close();
        JSONObject response = (JSONObject) jsonObject.get("response");
        return response;

    }
}
