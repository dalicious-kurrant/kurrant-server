package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.Spicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;

import java.time.LocalDate;

@Schema(description = "장바구니 조회 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    Integer id;
    Integer dailyFoodId;
    String name;
    Integer price;
    String img;
    String spicy;
    Makers makers;
    String diningType;
    Integer count;
    Integer sumPrice;
    Double discountRate;
    LocalDate serviceDate;


    @Builder
    public CartItemDto(OrderCartItem orderCartItem, Integer price, Integer dailyFoodId){
        this.id = orderCartItem.getFood().getId();
        this.name = orderCartItem.getFood().getName();
        this.price = orderCartItem.getFood().getPrice();
        this.img = orderCartItem.getFood().getImg();
        this.spicy = orderCartItem.getFood().getSpicy().getSpicy();
        this.makers = orderCartItem.getFood().getMakers();
        this.diningType = orderCartItem.getDiningType().getDiningType();
        this.count = orderCartItem.getCount();
        this.discountRate = orderCartItem.getFood().getDiscountedRate();
        this.serviceDate = orderCartItem.getServiceDate();
        this.sumPrice = price;
        this.dailyFoodId = dailyFoodId;
    }
}
