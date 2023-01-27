package co.dalicious.domain.payment.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

@Component
public class TossUtil {

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
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/card"))
                .header("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cardNumber\":\""+cardNumber+"\",\"cardExpirationYear\":\""+expirationYear+"\",\"cardExpirationMonth\":\""+expirationMonth+"\",\"cardPassword\":\""+cardPassword+"\",\"customerIdentityNumber\":\""+identityNumber+"\",\"customerKey\":\""+customerKey+"\"}"))
                .build();
    }

    public HttpResponse<String> payToCard(String customerKey, Integer amount,String orderId, String orderName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/{billingKey}"))
                .header("Authorization", "Basic dGVzdF9za19ZWjFhT3dYN0s4bWdwYnEyUjRRVnlReHp2TlBHOg==")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"customerKey\":\""+customerKey+"\",\"amount\":"+amount+",\"orderId\":\""+orderId+"\",\"orderName\":\""+orderName+"\"}"))
                .build();
       return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

}
