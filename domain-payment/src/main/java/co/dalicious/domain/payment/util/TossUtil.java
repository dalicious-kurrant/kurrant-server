package co.dalicious.domain.payment.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

@Component
@PropertySource("classpath:application-payment.properties")
public class TossUtil {

    private String secretKey;
    private String billingSecretKey;

    TossUtil(@Value("${toss.secret-key}") String secretKey,
             @Value("${toss.billing-secret-key}") String billingSecretKey){
        this.secretKey = secretKey;
        this.billingSecretKey = billingSecretKey;
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
                          String cardPassword, String identityNumber, String customerKey) throws IOException, ParseException {
        byte[] secretKeyToByte = billingSecretKey.getBytes();

        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKeyToByte);
        String authorizations = "Basic "+ new String(encodeByte, 0, encodeByte.length);

        URL url = new URL("https://api.tosspayments.com/v1/billing/authorizations/card");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("cardNumber", cardNumber);
        obj.put("cardExpirationYear", expirationYear);
        obj.put("cardExpirationMonth", expirationMonth);
        obj.put("cardPassword", cardPassword);
        obj.put("customerIdentityNumber", identityNumber);
        obj.put("customerKey", customerKey);

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

//        return HttpRequest.newBuilder()
//                .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/card"))
//                .header("Authorization", "Basic " +new String(encodeByte))
//                .header("Content-Type", "application/json")
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cardNumber\":\""+cardNumber+"\",\"cardExpirationYear\":\""+expirationYear+"\",\"cardExpirationMonth\":\""+expirationMonth+"\",\"cardPassword\":\""+cardPassword+"\",\"customerIdentityNumber\":\""+identityNumber+"\",\"customerKey\":\""+customerKey+"\"}"))
//                .build();
    }

    //카드 자동결제
    public JSONObject payToCard(String customerKey, Integer amount,String orderId, String orderName, String billingKey) throws IOException, InterruptedException, ParseException {

        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(billingSecretKey.getBytes("UTF-8"));
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
    }

    //카드 결제 부분취소
    public JSONObject cardCancelOne(String paymentKey, String cancelReason, Integer cancelAmount) throws IOException, ParseException {

        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic "+ new String(encodeByte, 0, encodeByte.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);


        //요청 JSON 오브젝트 생성
        JSONObject obj = new JSONObject();
        obj.put("cancelReason", cancelReason);
        obj.put("cancelAmount", cancelAmount);

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

    //결제승인
    public JSONObject paymentConfirm(String paymentKey, Integer amount, String orderId) throws IOException, ParseException {
        Base64.Encoder encode = Base64.getEncoder();
        byte[] encodeByte = encode.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic "+ new String(encodeByte, 0, encodeByte.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("paymentKey", paymentKey);
        obj.put("amount", amount);
        obj.put("orderId", orderId);

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


        /*
        * HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
    .header("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
    .header("Content-Type", "application/json")
    .method("POST", HttpRequest.BodyPublishers.ofString("{\"paymentKey\":\"3R6B0i8FCb4myEgVQSDK0\",\"amount\":15000,\"orderId\":\"x5Qmqd0sA2RR1WQT1sUd_\"}"))
    .build();
HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
        * */




}
