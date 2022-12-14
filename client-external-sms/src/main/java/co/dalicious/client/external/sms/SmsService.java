package co.dalicious.client.external.sms;

import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.client.external.sms.dto.SmsResponseDto;
import co.dalicious.system.util.RequiredAuth;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface SmsService {
    // Access Key Id와 맵핑되는 SecretKey로 암호화한 서명
    String getSignature(String time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException;
    // 메세지 전송
    SmsResponseDto sendSms(SmsMessageRequestDto smsMessageRequestDto, String content) throws JsonProcessingException, RestClientException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException;
}
