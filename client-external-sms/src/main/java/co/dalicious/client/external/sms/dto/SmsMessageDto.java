package co.dalicious.client.external.sms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SmsMessageDto {
    private String to;
    private String content;
    @Builder
    public SmsMessageDto(String to, String content) {
        this.to = to;
        this.content = content;
    }
}