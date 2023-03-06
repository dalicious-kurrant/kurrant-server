package co.dalicious.client.oauth;

import co.dalicious.domain.user.entity.enums.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;


@Service
public class SnsLoginServiceImpl implements SnsLoginService{
    @Override
    public SnsLoginResponseDto getSnsLoginUserInfo(Provider provider, String accessToken) {
        return switch (provider) {
            case NAVER -> getNaverLoginUserInfo(accessToken);
            case KAKAO -> getKakaoLoginUserInfo(accessToken);
            case GOOGLE -> getGoogleLoginUserInfo(accessToken);
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
                .phone(response.getResponse().getMobile().replaceAll("-", ""))
                .email(response.getResponse().getEmail())
                .name(response.getResponse().getName() == null ? "이름없음" : response.getResponse().getName())
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
                .name(response.getKakao_account().getName() == null ? "이름없음" : response.getKakao_account().getName())
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
                .name(googleResponse.getName() == null ? "이름없음" : googleResponse.getName().replaceAll(" ", ""))
                .build();
    }

    @Override
    public SnsLoginResponseDto getAppleLoginUserInfo(Map<String,Object> appleLoginDto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String idToken;
        String name;

        if(appleLoginDto == null) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }
        if(appleLoginDto.get("user") instanceof String) {
            AppleIPhoneLoginDto appleIPhoneLoginDto = mapper.convertValue(appleLoginDto, AppleIPhoneLoginDto.class);
            idToken = appleIPhoneLoginDto.getIdentityToken();
            if(appleIPhoneLoginDto.getFullName().getFamilyName() != null && appleIPhoneLoginDto.getFullName().getGivenName() != null) {
                name = appleIPhoneLoginDto.getFullName().getFamilyName() + appleIPhoneLoginDto.getFullName().getGivenName();
            }
            else {
                name = null;
            }
        }
        else {
            AppleAndroidLoginDto appleAndroidLoginDto = mapper.convertValue(appleLoginDto, AppleAndroidLoginDto.class);
            idToken = appleAndroidLoginDto.getId_token();
            name = (appleAndroidLoginDto.getUser() == null) ?
                    null : appleAndroidLoginDto.getUser().getName().getLastName() + appleAndroidLoginDto.getUser().getName().getFirstName();
        }

        String payloadJWT = idToken.split("\\.")[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));


        Map<String, Object> returnMap = mapper.readValue(payload, Map.class);

        String email = (String) returnMap.get("email");

        return SnsLoginResponseDto.builder()
                .email(email)
                .name((name == null) ? "이름없음" : name)
                .build();
    }

    @Override
    public SnsLoginResponseDto getFacebookLoginUserInfo(String accessToken) {
        // 헤더에 응답으로 받은 구글 계정정보 받아오기 위한 Access Token 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        accessToken = "Bearer " + accessToken;
        headers.set("Authorization", accessToken);

        // HttpEntity 생성.
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        // Send the request and retrieve the response
        FacebookLoginResponseDto response = restTemplate.postForEntity(
                "https://graph.facebook.com/v15.0/me?fields=id,name,email", requestEntity, FacebookLoginResponseDto.class).getBody();

        if(response == null) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }

        return SnsLoginResponseDto.builder()
                .email(response.getEmail())
                .name(response.getName() == null ? "이름없음" : response.getName())
                .build();
    }
}
