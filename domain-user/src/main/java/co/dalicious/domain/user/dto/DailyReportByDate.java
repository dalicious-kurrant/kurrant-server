package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "날짜별 영양정보")
public class DailyReportByDate {
    private String eatDate;
    private Integer calorie;
    private Integer protein;
    private Integer fat;
    private Integer carbohydrate;

    public DailyReportByDate(LocalDate date, Integer calorie, Integer protein, Integer fat, Integer carbohydrate) {
        this.eatDate = date.toString();
        this.calorie = calorie;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
    }
}
