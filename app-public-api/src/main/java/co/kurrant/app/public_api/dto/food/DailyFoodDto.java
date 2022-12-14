package co.kurrant.app.public_api.dto.food;

import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.Spicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "식단 응답 DTO")
@Getter
@NoArgsConstructor
public class DailyFoodDto {
    Integer id;
    String makers;
    String name;
    Integer price;
    String description;
    DiningType diningType;
    String img;
    Spicy spicy;
    Integer isSoldOut;

    @Builder
    DailyFoodDto(Integer id, String makers, String name, Integer price, String description, DiningType diningType, String img, Spicy spicy, Integer isSoldOut){
        this.id = id;
        this.makers = makers;
        this.name = name;
        this.price = price;
        this.description = description;
        this.diningType = diningType;
        this.img = img;
        this.spicy  = spicy;
        this.isSoldOut = isSoldOut;
    }
}
