package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "식단 리포트를 저장하기 위한 Dto")
@NoArgsConstructor
public class SaveDailyReportDto {

    private String name;
    private Integer calorie;
    private Integer carbohydrate;
    private Integer protein;
    private Integer fat;
    private String eatDate;
    private Integer diningType;

    @Builder
    SaveDailyReportDto(String name, Integer calorie, Integer carbohydrate, Integer protein, Integer fat, String eatDate, Integer diningType) {
        this.name = name;
        this.calorie = calorie;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.eatDate = eatDate;
        this.diningType = diningType;
    }
}
