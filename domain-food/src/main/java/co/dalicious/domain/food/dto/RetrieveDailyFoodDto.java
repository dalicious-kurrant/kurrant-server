package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "식단 조회 응답 Dto")
public class RetrieveDailyFoodDto {
    List<Integer> diningTypes;
    List<DailyFoodDto> dailyFoodDtos;

    @Builder
    public RetrieveDailyFoodDto(List<Integer> diningTypes, List<DailyFoodDto> dailyFoodDtos) {
        this.diningTypes = diningTypes;
        this.dailyFoodDtos = dailyFoodDtos;
    }
}
