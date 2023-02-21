package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class PresetScheduleRequestDto {
    private List<MakersScheduleDto> makersScheduleDtos;
    private List<FoodScheduleDto> foodScheduleDtos;

    @Getter
    @Setter
    public static class MakersScheduleDto {
        private BigInteger presetMakersId;
        private Integer scheduleStatus;
    }

    @Getter
    @Setter
    public static class FoodScheduleDto {
        private BigInteger presetFoodId;
        private Integer scheduleStatus;

    }
}
