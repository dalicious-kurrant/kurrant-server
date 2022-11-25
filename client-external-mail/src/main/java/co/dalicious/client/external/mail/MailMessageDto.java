package co.dalicious.client.external.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class MailMessageDto {
    private List<String> receivers;
//    String content;
    @Builder
    public MailMessageDto(List<String> receivers) {
        this.receivers = receivers;
    }
}