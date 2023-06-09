package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DailyReport중 식사 히스토리에 필요한 응답 dto")
public class MealHistoryResDto {

    private List<DailyReportByDate> DailyReportList;

}
