package co.dalicious.client.external.sms;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SmsMessageDto {
    private String to;
//    String content;
    @Builder
    public SmsMessageDto(String to) {
        this.to = to;
    }
}