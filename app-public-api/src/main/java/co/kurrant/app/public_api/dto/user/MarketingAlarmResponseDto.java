package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Schema(description = "마케팅 수신정보 변경 응답 DTO")
@Getter
@NoArgsConstructor
<<<<<<<< HEAD:app-public-api/src/main/java/co/kurrant/app/public_api/dto/user/MarketingAlarmResponseDto.java
public class MarketingAlarmResponseDto {
    private String marketingAgreedDateTime;
========
public class MarketingAlarmRequestDto {
    private Timestamp marketingAgreedDateTime;
>>>>>>>> dev:app-public-api/src/main/java/co/kurrant/app/public_api/dto/user/MarketingAlarmRequestDto.java

    private Boolean marketingAgree;

    private Boolean marketingAlarm;

    private Boolean orderAlarm;

    @Builder
<<<<<<<< HEAD:app-public-api/src/main/java/co/kurrant/app/public_api/dto/user/MarketingAlarmResponseDto.java
    public MarketingAlarmResponseDto(String marketingAgreedDateTime, Boolean marketingAgree, Boolean marketingAlarm, Boolean orderAlarm) {
========
    public MarketingAlarmRequestDto(Timestamp marketingAgreedDateTime, Boolean marketingAgree, Boolean marketingAlarm, Boolean orderAlarm) {
>>>>>>>> dev:app-public-api/src/main/java/co/kurrant/app/public_api/dto/user/MarketingAlarmRequestDto.java
        this.marketingAgreedDateTime = marketingAgreedDateTime;
        this.marketingAgree = marketingAgree;
        this.marketingAlarm = marketingAlarm;
        this.orderAlarm = orderAlarm;
    }
}
