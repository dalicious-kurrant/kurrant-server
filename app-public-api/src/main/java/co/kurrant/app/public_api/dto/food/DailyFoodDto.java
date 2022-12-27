package co.kurrant.app.public_api.dto.food;

import co.dalicious.domain.food.entity.Food;
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

    String foodName;
    Boolean isSoldOut;
    Integer spotId;
    FoodStatus status;
    LocalDate updated;

    LocalDate serviceDate;

    @Builder
    public DailyFoodDto(Integer id, LocalDate created, DiningType diningType, Food food,
                        Boolean isSoldOut, Integer spotId, FoodStatus status, LocalDate updated,
                        LocalDate serviceDate){
        this.id = id;
        this.created = created;
        this.diningType = diningType;
        this.foodId = food.getId();
        this.foodName = food.getName();
        this.isSoldOut = isSoldOut;
        this.spotId = spotId;
        this.status = status;
        this.updated = updated;
        this.serviceDate = serviceDate;
    }
}
