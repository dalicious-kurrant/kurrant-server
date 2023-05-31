package co.kurrant.app.public_api.dto.order;

import co.dalicious.system.enums.DiningType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "OrderDailyFood를 DailyReport로 변환하기 위한 Dto")
public class OrderItemDailyFoodToDailyReportDto {

    private String title;
    private String name;
    private Integer carbohydrate;
    private Integer fat;
    private Integer protein;
    private Integer calorie;
    private LocalDate eatDate;
    private DiningType diningType;


}
