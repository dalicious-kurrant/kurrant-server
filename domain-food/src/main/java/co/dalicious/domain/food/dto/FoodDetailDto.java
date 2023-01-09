package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.util.OriginList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "식품 상세정보 DTO")
@Getter
@Setter
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
