package co.dalicious.data.redis.sms;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MessageDto {
    private String to;
//    String content;
    @Builder
    public MessageDto(String to) {
        this.to = to;
    }
}