package co.kurrant.app.admin_api.dto.schedules;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
public class ExcelPresetDto {
    private String makersName;
    private LocalDate serviceDate;
    private DiningType diningType;
    private Integer makersCapacity;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExcelGroupDataDto {

        private String groupName;
        private String makersName;
        private LocalDate serviceDate;
        private DiningType diningType;

        public static ExcelGroupDataDto createExcelGroupDto(ExcelPresetDailyFoodDto.ExcelData data) {
            return ExcelGroupDataDto.builder()
                    .groupName(data.getGroupName())
                    .serviceDate(DateUtils.stringToDate(data.getServiceDate()))
                    .diningType(DiningType.ofString(data.getDiningType()))
                    .makersName(data.getMakersName()).build();
        }

        public static ExcelGroupDataDto createExcelGroupDto(PresetMakersDailyFood makersDailyFood, PresetGroupDailyFood groupDailyFood) {
            return ExcelGroupDataDto.builder()
                    .groupName(groupDailyFood.getGroup().getName())
                    .serviceDate(makersDailyFood.getServiceDate())
                    .diningType(makersDailyFood.getDiningType())
                    .makersName(makersDailyFood.getMakers().getName()).build();
        }

        public static ExcelGroupDataDto createExcelGroupDto(PresetMakersDailyFood makersDailyFood, Group group) {
            return ExcelGroupDataDto.builder()
                    .groupName(group.getName())
                    .serviceDate(makersDailyFood.getServiceDate())
                    .diningType(makersDailyFood.getDiningType())
                    .makersName(makersDailyFood.getMakers().getName()).build();
        }

        public boolean equals(Object obj) {
            if(obj instanceof ExcelGroupDataDto tmp) {
                return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType) && groupName.equals(tmp.groupName) && makersName.equals(tmp.makersName);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(serviceDate, diningType, groupName, makersName);
        }

    }

    public static ExcelPresetDto createExcelPresetDto(ExcelPresetDailyFoodDto.ExcelData data) {
        return ExcelPresetDto.builder()
                .serviceDate(DateUtils.stringToDate(data.getServiceDate()))
                .diningType(DiningType.ofString(data.getDiningType()))
                .makersName(data.getMakersName())
                .makersCapacity(data.getMakersCapacity()).build();
    }

    public static ExcelPresetDto createExcelPresetDto(PresetMakersDailyFood data) {
        return ExcelPresetDto.builder()
                .serviceDate(data.getServiceDate())
                .diningType(data.getDiningType())
                .makersName(data.getMakers().getName()).build();
    }

    public boolean equals(Object obj) {
        if(obj instanceof ExcelPresetDto tmp) {
            return serviceDate.equals(tmp.serviceDate) && diningType.equals(tmp.diningType) && makersName.equals(tmp.makersName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(serviceDate, diningType, makersName);
    }

}

