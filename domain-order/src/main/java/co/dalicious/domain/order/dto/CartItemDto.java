package co.dalicious.domain.order.dto;

import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.system.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;

@Schema(description = "장바구니 조회 DTO")
@Getter
@Setter
public class CartItemDto {
    BigInteger id;
    BigInteger dailyFoodId;
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
