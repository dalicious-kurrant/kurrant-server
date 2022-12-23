package co.dalicious.client.oauth;

import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SnsLoginServiceImpl implements SnsLoginService{
    @Override
    public SnsLoginResponseDto getNaverLoginUserInfo(String accessToken) {
        // 헤더에 응답으로 받은 네이버 계정정보 받아오기 위한 Access Token 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        accessToken = "Bearer " + accessToken;
        headers.set("Authorization", accessToken);

        // HttpEntity 생성.
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        // Send the request and retrieve the response
        NaverLoginResponseDto response = restTemplate.postForEntity(
                "https://openapi.naver.com/v1/nid/me", requestEntity, NaverLoginResponseDto.class).getBody();

        assert response != null;

        if(!response.getResultcode().equals("00")) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }
        return SnsLoginResponseDto.builder()
                .phone(response.getResponse().mobile.replaceAll("-", ""))
                .email(response.getResponse().email)
                .name(response.getResponse().name)
                .build();
    }

    @Override
    public SnsLoginResponseDto getKakaoLoginUserInfo(String accessToken) {
        return null;
    }

    @Override
    public SnsLoginResponseDto getGoogleLoginUserInfo(String accessToken) {
        return null;
    }

    @Override
    public SnsLoginResponseDto getAppleLoginUserInfo(String accessToken) {
        return null;
    }

    @Override
    public SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken) {
        return null;
    }
}
