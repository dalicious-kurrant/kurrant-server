package co.dalicious.client.oauth;

import co.dalicious.domain.user.entity.enums.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class SnsLoginServiceImpl implements SnsLoginService{
    @Override
    public SnsLoginResponseDto getSnsLoginUserInfo(Provider provider, String accessToken) {
        return switch (provider) {
            case NAVER -> getNaverLoginUserInfo(accessToken);
            case KAKAO -> getKakaoLoginUserInfo(accessToken);
            case GOOGLE -> getGoogleLoginUserInfo(accessToken);
            case APPLE -> getAppleLoginUserInfo(accessToken);
            case FACEBOOK -> getFacebookLoginUserInfo(accessToken);
            default -> null;
        };
    }

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
        // 헤더에 응답으로 받은 카카오 계정정보 받아오기 위한 Access Token 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        accessToken = "Bearer " + accessToken;
        headers.set("Authorization", accessToken);

        // HttpEntity 생성.
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        // Send the request and retrieve the response
        KakaoLoginResponseDto response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/user/me", requestEntity, KakaoLoginResponseDto.class).getBody();

        if(response == null) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }

        return SnsLoginResponseDto.builder()
                .phone("0" + response.getKakao_account().getPhone_number().substring(4).replaceAll("-", ""))
                .email(response.getKakao_account().getEmail())
                .name(response.getKakao_account().getName())
                .build();
    }

    @Override
    public SnsLoginResponseDto getGoogleLoginUserInfo(String accessToken) {
        // 헤더에 응답으로 받은 구글 계정정보 받아오기 위한 Access Token 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        accessToken = "Bearer " + accessToken;
        headers.set("Authorization", accessToken);

        // HttpEntity 생성.
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        // Send the request and retrieve the response
        ResponseEntity<GoogleLoginResponseDto> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo", HttpMethod.GET, requestEntity, GoogleLoginResponseDto.class);
        if(response.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }
        GoogleLoginResponseDto googleResponse = response.getBody();
        assert googleResponse != null;

        return SnsLoginResponseDto.builder()
                .email(googleResponse.getEmail())
                .name(googleResponse.getName().replaceAll(" ", ""))
                .build();
    }

    @Override
    public SnsLoginResponseDto getAppleLoginUserInfo(String accessToken) {
        return null;
    }

    @Override
    public SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken) {
        return null;
    }

    public Payload decodeFromIdToken(String id_token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            JWTClaimsSet getPayload = SignedJWT.parse(id_token).getJWTClaimsSet();
            ObjectMapper objectMapper = new ObjectMapper();
            Payload payload = objectMapper.readValue(getPayload.toJSONObject().toString(), Payload.class);
            System.out.println(payload);

            if (payload != null) {
                return payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
