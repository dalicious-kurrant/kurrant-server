package co.dalicious.domain.application_form.dto.corporation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "기업 스팟 개설 신청 옵션 요청/응답 DTO")
public class CorporationOptionsDto {
    private Boolean isGarbage;
    private Boolean isHotStorage;
    private Boolean isSetting;
    private String memo;
}
