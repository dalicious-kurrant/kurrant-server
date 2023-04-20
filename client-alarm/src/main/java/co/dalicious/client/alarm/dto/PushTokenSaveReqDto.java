package co.dalicious.client.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "FCM 토큰 저장 요청 Dto")
public class PushTokenSaveReqDto {

    private BigInteger userId;
    private String token;

}
