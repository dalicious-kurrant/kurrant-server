package co.kurrant.app.admin_api.dto.schedules;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExcelPresetDailyFoodDto {
    private String deadline;
    private List<ExcelData> excelDataList;

    @Getter
    @Setter
    public static class ExcelData {
        private String makersName;
        private Integer makersScheduleStatus;
        private String serviceDate;
        private String diningType;
        private Integer makersCapacity;
        private String pickupTime;
        private String groupName;
        private Integer groupCapacity;
        private Integer leftMakersCapacity;
        private Integer foodScheduleStatus;
        private String foodName;
        private String foodStatus;
        private Integer foodCapacity;
        private Integer leftFoodCapacity;

    }

}
