package co.kurrant.app.public_api.dto.food;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "식단 응답 DTO")
@Getter
public class DailyFoodDto {
    BigInteger id;
    String created;
    String diningType;
    BigInteger foodId;
    String foodName;
    Boolean isSoldOut;
    BigInteger spotId;
    String status;
    String updated;
    String serviceDate;
    String makersName;
    Integer price;
    String spicy;
    String img;
    String description;
    Integer discountedPrice;
    Integer discountRate;

    @Builder
    public DailyFoodDto(BigInteger id, LocalDate created, String diningType, Food food,
                        Boolean isSoldOut, BigInteger spotId, String status, LocalDate updated,
                        LocalDate serviceDate, Integer discountedPrice, Integer discountRate){
        this.id = id;
        this.created = DateUtils.format(created, "yyyy-MM-dd");
        this.diningType = diningType;
        this.foodId = food.getId();
        this.foodName = food.getName();
        this.isSoldOut = isSoldOut;
        this.spotId = spotId;
        this.status = status;
        this.updated = DateUtils.format(updated, "yyyy-MM-dd");
        this.serviceDate = DateUtils.format(serviceDate, "yyyy-MM-dd");
        this.makersName = food.getMakers().getName();
        this.price = food.getPrice();
        this.spicy = food.getSpicy().getSpicy();
        this.img = food.getImg();
        this.description = food.getDescription();
        this.discountedPrice = discountedPrice;
        this.discountRate = discountRate;
    }
}
