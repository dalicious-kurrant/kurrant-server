package co.kurrant.app.admin_api.dto.alimtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "알림톡 테스트용 Dto")
public class AlimtalkTestDto {
    private String phoneNumber;
    private String templateId;

}
