package co.kurrant.app.public_api.dto.food;

import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.FoodStatus;
import co.dalicious.system.util.Spicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "식단 응답 DTO")
@Getter
@NoArgsConstructor
public class DailyFoodDto {
    Integer id;
    LocalDate created;
    DiningType diningType;
    Integer foodId;
    Integer isSoldOut;
    Integer spotId;
    FoodStatus status;
    LocalDate updated;

    @Builder
    public DailyFoodDto(Integer id, LocalDate created, DiningType diningType, Integer foodId, Integer isSoldOut, Integer spotId, FoodStatus status, LocalDate updated){
        this.id = id;
        this.created = created;
        this.diningType = diningType;
        this.foodId = foodId;
        this.isSoldOut = isSoldOut;
        this.spotId = spotId;
        this.status = status;
        this.updated = updated;
    }
}
