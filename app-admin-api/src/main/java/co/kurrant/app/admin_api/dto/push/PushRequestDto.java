package co.kurrant.app.admin_api.dto.push;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "FCM 토큰으로 PUSH 보내기 요청 DTO")
public class PushRequestDto {

    private List<String> tokenList;
    private String title;
    private String content;

}