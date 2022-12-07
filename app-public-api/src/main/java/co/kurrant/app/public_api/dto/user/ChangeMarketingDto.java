package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Schema(description = "마케팅 수신정보 변경 요청/응답 DTO")
@Getter
@NoArgsConstructor
public class ChangeMarketingDto {
    private Timestamp marketingAgreedDateTime;

    private Boolean marketingAgree;

    private Boolean marketingAlarm;

    private Boolean orderAlarm;

    @Builder
    public ChangeMarketingDto(Timestamp marketingAgreedDateTime, Boolean marketingAgree, Boolean marketingAlarm, Boolean orderAlarm) {
        this.marketingAgreedDateTime = marketingAgreedDateTime;
        this.marketingAgree = marketingAgree;
        this.marketingAlarm = marketingAlarm;
        this.orderAlarm = orderAlarm;
    }
}
