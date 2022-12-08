package co.kurrant.app.public_api.dto.food;

import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.Spicy;
import io.swagger.v3.oas.annotations.media.Schema;
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
    Boolean isSoldOut;
}
