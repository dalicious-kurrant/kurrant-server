package co.dalicious.domain.order.dto;

import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
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
    Makers makers;
    String diningType;
    Integer count;
    Integer sumPrice;
    Double discountRate;
    String serviceDate;
    BigDecimal supportPrice;
    BigDecimal deliveryFee;
    BigDecimal membershipPrice;
    BigDecimal discountPrice;
    BigDecimal periodDiscountPrice;



    @Builder
    public CartItemDto(OrderCartItem orderCartItem, Integer price, BigDecimal supportPrice, BigDecimal deliveryFee,
                       BigDecimal membershipPrice, BigDecimal discountPrice, BigDecimal periodDiscountPrice, BigDecimal discountRate){
        this.id = orderCartItem.getId();
        this.name = orderCartItem.getDailyFood().getFood().getName();
        this.price = orderCartItem.getDailyFood().getFood().getPrice();
        this.img = orderCartItem.getDailyFood().getFood().getImg();
        this.makers = orderCartItem.getDailyFood().getFood().getMakers();
        this.diningType = orderCartItem.getDiningType().getDiningType();
        this.count = orderCartItem.getCount();
        this.discountRate = discountRate.doubleValue();
        this.serviceDate = DateUtils.format(orderCartItem.getServiceDate(), "yyyy-MM-dd");
        this.sumPrice = price;
        this.dailyFoodId = orderCartItem.getDailyFood().getId();
        this.supportPrice = supportPrice;
        this.deliveryFee = deliveryFee;
        this.membershipPrice = membershipPrice;
        this.discountPrice = discountPrice;
        this.periodDiscountPrice = periodDiscountPrice;
    }
}
