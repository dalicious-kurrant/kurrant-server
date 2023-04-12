package co.dalicious.client.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Schema(description = "FCM 토큰으로 PUSH 보내기 요청 DTO")
public class PushRequestDto {

    private List<String> tokenList;
    private String title;
    private String content;
    private String page;
    private Map<String, String> keys;

}
