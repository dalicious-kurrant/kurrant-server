package co.dalicious.domain.user.dto.pointPolicyResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "식단 리포트를 저장하기 위한 Dto")
public class SaveDailyReportDto {

    private String name;
    private Integer calorie;
    private Integer carbohydrate;
    private Integer protein;
    private Integer fat;
    private String eatDate;

}
