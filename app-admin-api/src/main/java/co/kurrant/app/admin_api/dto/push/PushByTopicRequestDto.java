package co.kurrant.app.admin_api.dto.push;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "주제별로 푸쉬보내기")
public class PushByTopicRequestDto {

    @Schema(description = "push/event/notice")
    private String topic;
    @Schema(description = "제목")
    private String title;
    @Schema(description = "내용")
    private String content;

}
