package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.system.util.DateUtils;
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
    String serviceDate;


    @Builder
    public CartItemDto(OrderCartItem orderCartItem, Integer price){
        this.id = orderCartItem.getOrderCart().getId();
        this.name = orderCartItem.getDailyFood().getFood().getName();
        this.price = orderCartItem.getDailyFood().getFood().getPrice();
        this.img = orderCartItem.getDailyFood().getFood().getImg();
        this.spicy = orderCartItem.getDailyFood().getFood().getSpicy().getSpicy();
        this.makers = orderCartItem.getDailyFood().getFood().getMakers();
        this.diningType = orderCartItem.getDiningType().getDiningType();
        this.count = orderCartItem.getCount();
        this.discountRate = orderCartItem.getDailyFood().getFood().getDiscountedRate();
        this.serviceDate = DateUtils.format(orderCartItem.getServiceDate(), "yyyy-MM-dd");
        this.sumPrice = price;
        this.dailyFoodId = orderCartItem.getDailyFood().getId();
    }
}
