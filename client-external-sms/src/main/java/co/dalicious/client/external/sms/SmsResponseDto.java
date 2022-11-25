package co.dalicious.client.external.sms;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SmsResponseDto {
    private String requestId;
    private LocalDateTime requestTime;
    private String statusCode;
    private String statusName;
    private String smsConfirmNum;

    @Builder
    public SmsResponseDto(String requestId, LocalDateTime requestTime, String statusCode, String statusName, String smsConfirmNum) {
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.smsConfirmNum = smsConfirmNum;
    }
}
