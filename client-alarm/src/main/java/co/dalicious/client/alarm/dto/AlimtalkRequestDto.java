package co.dalicious.client.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "알림톡 발송 요청 DTO")
public class AlimtalkRequestDto {

    private String phoneNumber;
    private String templateId;
    private String content;

    public AlimtalkRequestDto(String phoneNumber, String templateId, String content) {
        this.phoneNumber = phoneNumber;
        this.templateId = templateId;
        this.content = content;
    }
}
