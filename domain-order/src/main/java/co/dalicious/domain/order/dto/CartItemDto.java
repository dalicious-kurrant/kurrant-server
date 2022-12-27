package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.system.util.DiningType;
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
    Food food;
    DiningType diningType;
    Integer count;
    Integer price;
    Double discountRate;
    LocalDate serviceDate;

    @Builder
    public CartItemDto(OrderCartItem orderCartItem, Integer price){
        this.food = orderCartItem.getFoodId();
        this.diningType = orderCartItem.getDiningType();
        this.count = orderCartItem.getCount();
        this.discountRate = Double.valueOf(orderCartItem.getFoodId().getDiscountedRate());//할인율로 변경필요
        this.serviceDate = orderCartItem.getServiceDate();
        this.price = price;
    }
}
