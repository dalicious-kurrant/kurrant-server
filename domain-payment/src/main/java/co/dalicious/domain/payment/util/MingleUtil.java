package co.dalicious.domain.payment.util;

import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application-payment.properties")
public class MingleUtil {
    private final String mid;
    private final String merchantKey;

    MingleUtil(@Value("${mingle.mid}") String mid,
               @Value("${mingle.merchantKey}") String merchantKey) {
        this.mid = mid;
        this.merchantKey = merchantKey;
    }

    // 빌링키 발급
    public JSONObject generateBillingKey(String corporationCode, String cardType, String cardNumber, String expirationYear, String expirationMonth, String identityNumber, String cardPassword) throws IOException, ParseException {
        URL url = new URL("https://pg.minglepay.co.kr/payment.registBill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String ediDate = DateUtils.format(LocalDateTime.now(), "yyyyMMddhhmmss");

        Map<String, String> request = new HashMap<>();
        request.put("PayMethod", "CARD");
        request.put("trxCd", "0");
        request.put("mid", mid);
        request.put("cpCd", corporationCode);
        request.put("cardTypeCd", cardType);
        request.put("cardNo", cardNumber);
        request.put("expireYymm", expirationYear + expirationMonth);
        request.put("ordAuthNo", identityNumber);
        request.put("cardPw", cardPassword);
        request.put("ordNo", ediDate);
        request.put("ordTel", "123");
        request.put("ordNm", "김민지");
        request.put("goodsNm", "빌링키 발급");
        request.put("goodsAmt", "0");
        request.put("quotaMon", "00");
        request.put("noIntFlg", "0");
        request.put("pointFlg", "0");
        request.put("reqType", "0");
        request.put("ediDate", ediDate);
        request.put("encData", mid + ediDate + "0" + merchantKey);

        String requestData = toFormUrlEncoded(request);

        byte[] requestDataBytes = requestData.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(requestDataBytes.length));

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestDataBytes);
        }

        int code = connection.getResponseCode();

        InputStream responseStream = code == 200 ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        System.out.println(jsonObject);
        return jsonObject;
    }

    // 빌링키 삭제
    public JSONObject deleteBillingKey(String bid) throws IOException, ParseException {
        URL url = new URL("https://pg.minglepay.co.kr/payment.deleteBill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String ediDate = DateUtils.format(LocalDateTime.now(), "yyyyMMddhhmmss");

        Map<String, String> request = new HashMap<>();
        request.put("bid", bid);
        request.put("mid", mid);
        request.put("ediDate", ediDate);
        request.put("encData", mid + ediDate + "0" + merchantKey);

        String requestData = toFormUrlEncoded(request);

        byte[] requestDataBytes = requestData.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(requestDataBytes.length));

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestDataBytes);
        }

        int code = connection.getResponseCode();

        InputStream responseStream = code == 200 ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        System.out.println(jsonObject);
        return jsonObject;
    }


    private String toFormUrlEncoded(Map<String, String> data) {
        return data.entrySet().stream()
                .map(entry -> {
                    try {
                        return URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining("&"));
    }
}
