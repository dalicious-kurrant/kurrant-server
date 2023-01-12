package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.util.OriginList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "식품 상세정보 DTO")
@Getter
@Setter
public class FoodDetailDto {
    private String makersName;
    private String name;
    private Integer price;
    private Integer discountedPrice;
    private BigDecimal discountRate;
    private String img;
    private String spicy;
    private String description;
    private List<OriginList> originList;

    @Builder
    FoodDetailDto(Food food, List<OriginList> originList, Integer price, BigDecimal discountRate){
        this.makersName = food.getMakers().getName();
        this.name = food.getName();
        this.price = food.getPrice();
        this.discountedPrice = price;
        this.discountRate = discountRate;
        this.img = food.getImg();
        this.spicy = food.getSpicy().getSpicy();
        this.originList = originList;
        this.description = food.getDescription();
    }

}
