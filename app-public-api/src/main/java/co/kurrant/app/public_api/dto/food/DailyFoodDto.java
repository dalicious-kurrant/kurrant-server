package co.kurrant.app.public_api.dto.food;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.FoodStatus;
import co.dalicious.system.util.Spicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "식단 응답 DTO")
@Getter
@NoArgsConstructor
public class DailyFoodDto {
    BigInteger id;
    String created;
    String diningType;
    BigInteger foodId;

    String foodName;
    Boolean isSoldOut;
    Integer spotId;
    String status;
    String updated;

    String serviceDate;

    String makersName;
    Integer price;
    String spicy;
    String img;

    String description;



    @Builder
    public DailyFoodDto(BigInteger id, LocalDate created, DiningType diningType, Food food,
                        Boolean isSoldOut, Integer spotId, FoodStatus status, LocalDate updated,
                        LocalDate serviceDate, Makers makers){
        this.id = id;
        this.created = DateUtils.format(created, "yyyy-MM-dd");
        this.diningType = diningType.getDiningType();
        this.foodId = food.getId();
        this.foodName = food.getName();
        this.isSoldOut = isSoldOut;
        this.spotId = spotId;
        this.status = status.getStatus();
        this.updated = DateUtils.format(updated, "yyyy-MM-dd");
        this.serviceDate = DateUtils.format(serviceDate, "yyyy-MM-dd");
        this.makersName = makers.getName();
        this.price = food.getPrice();
        this.spicy = food.getSpicy().getSpicy();
        this.img = food.getImg();
        this.description = food.getDescription();
    }
}
