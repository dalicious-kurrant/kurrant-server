package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.util.OriginList;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.Spicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "식품 상세정보 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodDetailDto {
    private String makers;
    private String name;
    private Integer price;
    private String img;
    private String spicy;
    private List<OriginList> originList;

    @Builder
    FoodDetailDto(Food food, List<OriginList> origin){
        this.makers = food.getMakers().getName();
        this.name = food.getName();
        this.price = food.getPrice();
        this.img = food.getImg();
        this.spicy = food.getSpicy().getSpicy();
        this.originList = origin;
    }

}
