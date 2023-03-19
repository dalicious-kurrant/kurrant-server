package co.kurrant.app.makers_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DailyFoodDto {
    private String serviceDate;
    private List<DailyFoodDining> dailyFoodDiningList;

    @Getter
    @Setter
    public static class DailyFoodDining {
        private Integer diningType;
        private Integer groupCapacity;
        private List<DailyFood> dailyFoodList;
    }

    @Getter
    @Setter
    public static class DailyFood {
        private String foodName;
        private Integer foodCapacity;
    }
}
