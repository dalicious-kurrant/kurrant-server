package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "식단 리포트 응답 Dto")
public class DailyReportResDto {

    private Integer totalCalorie;
    private Integer totalCarbohydrate;
    private Integer totalFat;
    private Integer totalProtein;
    private List<FindDailyReportResDto> dailyReportResDtoList;


}
