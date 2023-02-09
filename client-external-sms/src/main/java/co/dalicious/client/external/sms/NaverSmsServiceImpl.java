package co.dalicious.client.external.sms;

import co.dalicious.client.external.sms.dto.SmsMessageDto;
import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.client.external.sms.dto.SmsRequestDto;
import co.dalicious.client.external.sms.dto.SmsResponseDto;
import co.dalicious.data.redis.CertificationHashRepository;
import co.dalicious.data.redis.RedisConfig;
import co.dalicious.system.util.GenerateRandomNumber;
import co.dalicious.system.util.RequiredAuth;
import exception.ApiException;
import exception.ExceptionEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.dalicious.data.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@PropertySource("classpath:application-sms.properties")
@Slf4j
@RequiredArgsConstructor
@Service
public class NaverSmsServiceImpl implements SmsService {
    private final RedisUtil redisUtil;
    private final CertificationHashRepository certificationHashRepository;
    @Value("${naver-cloud-sms.accessKey}")
    private String accessKey;

    @Value("${naver-cloud-sms.secretKey}")
    private String secretKey;

    @Value("${naver-cloud-sms.serviceId}")
    private String serviceId;

    @Value("${naver-cloud-sms.senderPhone}")
    private String phone;

    public String getSignature(String time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(time)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public SmsResponseDto sendSms(SmsMessageRequestDto smsMessageRequestDto, String content) throws JsonProcessingException, RestClientException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String time = Long.toString(System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", getSignature(time)); // signature 서명

        List<SmsMessageDto> messages = new ArrayList<>();
        SmsMessageDto smsMessageDto = SmsMessageDto.builder()
                .to(smsMessageRequestDto.getTo())
                .content(content)
                .build();
        messages.add(smsMessageDto);

        SmsRequestDto request = SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(phone)
                .content(content)
                .messages(messages)
                .build();

        //쌓은 바디를 json형태로 반환
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        // jsonBody와 헤더 조립
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        //restTemplate로 post 요청 보내고 오류가 없으면 202코드 반환
        try {
            return restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponseDto.class);
        } catch (RestClientException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}

