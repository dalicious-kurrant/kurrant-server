package co.dalicious.domain.order.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@NoArgsConstructor
@Table(name = "order__cart")
public class OrderCart {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "food_id")
    private Integer foodId;


    @Column(name = "created_datetime")
    @Comment("장바구니에 담은 시간")
    private Date created;

    @Column(name = "user_id")
    @Comment("유저 Id")
    private Integer userId;

    @Column(name = "total_price")
    private Integer totalPrice;

    @JoinColumn(name = "cart_id")
    private Integer cartId;

    @Builder
    public OrderCart(Integer id, Integer foodId, Date created, Integer userId, Integer totalPrice, Integer cartId){
        this.id = id;
        this.foodId = foodId;
        this.created= created;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.cartId = cartId;
    }
}