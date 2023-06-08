package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "날짜별 영양정보")
public class DailyReportByDate {
    private String date;
    private Integer calorie;
    private Integer protein;
    private Integer fat;
    private Integer carbohydrate;

}
