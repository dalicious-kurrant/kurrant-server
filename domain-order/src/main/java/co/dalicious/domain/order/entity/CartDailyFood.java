package co.dalicious.domain.order.entity;

import  co.dalicious.domain.food.entity.DailyFood;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order__cart_item")
public class CartDailyFood extends Cart {
    @ManyToOne
    @JoinColumn(name="dailyFood_id")
    @Comment("장바구니에 담긴 음식 ID")
    private DailyFood dailyFood;
}
