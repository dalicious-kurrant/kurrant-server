package co.dalicious.domain.order.entity;

import co.dalicious.domain.client.entity.Spot;
import  co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalTime;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order__cart_item")
public class CartDailyFood extends Cart {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="dailyFood_id")
    @Comment("장바구니에 담긴 음식 ID")
    private DailyFood dailyFood;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="spot_id")
    @Comment("장바구니에 담긴 음식 상품의 배송지")
    private Spot spot;

    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @Builder
    public CartDailyFood(User user, Integer count, DailyFood dailyFood, Spot spot, LocalTime deliveryTime) {
        super(user, count);
        this.dailyFood = dailyFood;
        this.spot = spot;
        this.deliveryTime = deliveryTime;
    }
}
