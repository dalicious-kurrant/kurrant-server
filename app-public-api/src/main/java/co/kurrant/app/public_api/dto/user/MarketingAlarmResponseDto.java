package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "마케팅 수신정보 변경 응답 DTO")
@Getter
@Setter
public class MarketingAlarmResponseDto {
    private Integer code;
    private String condition;
    private Boolean isActive;
}
