package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저정보에 FCM토큰을 저장하는 DTO")
public class FcmTokenSaveReqDto {
    private String token;
}
