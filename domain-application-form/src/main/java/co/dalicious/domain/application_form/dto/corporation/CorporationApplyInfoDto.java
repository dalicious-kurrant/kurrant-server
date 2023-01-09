package co.dalicious.domain.application_form.dto.corporation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "기업 스팟 개설 신청 DTO")
@Getter
@Setter
public class CorporationApplyInfoDto {
    private String corporationName;
    private Integer employeeCount;
    private String startDate;
    private List<String> diningTypes;
}
