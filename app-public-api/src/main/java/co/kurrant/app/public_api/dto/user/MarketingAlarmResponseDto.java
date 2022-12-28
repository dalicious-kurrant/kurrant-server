package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Schema(description = "마케팅 수신정보 변경 응답 DTO")
@Getter
@NoArgsConstructor
public class MarketingAlarmResponseDto {

    private Boolean marketingAgree;

    private Boolean marketingAlarm;

    private Boolean orderAlarm;

    @Builder
    public MarketingAlarmResponseDto(Boolean marketingAgree, Boolean marketingAlarm, Boolean orderAlarm) {
        this.marketingAgree = marketingAgree;
        this.marketingAlarm = marketingAlarm;
        this.orderAlarm = orderAlarm;
    }
}
