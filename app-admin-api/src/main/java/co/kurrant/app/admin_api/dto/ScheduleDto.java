package co.kurrant.app.admin_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ScheduleDto {
    @Getter
    @Setter
    public static class GroupSchedule {
        private String serviceDate;
        private Integer diningType;
        private String groupName;
        private Integer groupCapacity;
        private String deliveryTime;
        private List<MakersSchedule> makersSchedules;

    }

    @Getter
    @Setter
    public static class MakersSchedule {
        private String makersName;
        private Integer makersCapacity;
        private Integer makersCount;
        private String makersPickupTime;
        private List<FoodSchedule> foodSchedules;
    }

    @Getter
    @Setter
    public static class FoodSchedule {
        private BigInteger dailyFoodId;
        private String foodName;
        private Integer dailyFoodStatus;
        private Integer foodCapacity;
        private Integer foodCount;
    }
}
