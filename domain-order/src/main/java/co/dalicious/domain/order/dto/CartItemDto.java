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
    String name;
    Integer price;
    String img;
    Spicy spicy;
    Makers makers;
    String description;
    DiningType diningType;
    Integer count;
    Integer sumPrice;
    Double discountRate;
    LocalDate serviceDate;

    @Builder
    public CartItemDto(OrderCartItem orderCartItem, Integer price){
        this.id = orderCartItem.getFoodId().getId();
        this.name = orderCartItem.getFoodId().getName();
        this.price = orderCartItem.getFoodId().getPrice();
        this.img = orderCartItem.getFoodId().getImg();
        this.spicy = orderCartItem.getFoodId().getSpicy();
        this.makers = orderCartItem.getFoodId().getMakers();
        this.description = orderCartItem.getFoodId().getDescription();
        this.diningType = orderCartItem.getDiningType();
        this.count = orderCartItem.getCount();
        this.discountRate = orderCartItem.getFoodId().getDiscountedRate();
        this.serviceDate = orderCartItem.getServiceDate();
        this.sumPrice = price;
    }
}
