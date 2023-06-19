package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "식단 리포트 조회 응답 Dto")
public class FindDailyReportResDto {

    private BigInteger reportId;
    private Integer diningType;
    private String title;
    private String foodName;
    private Integer calorie;
    private Integer carbohydrate;
    private Integer protein;
    private Integer fat;
    private String imgLocation;

}
