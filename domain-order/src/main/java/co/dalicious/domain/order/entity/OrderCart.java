package co.dalicious.domain.order.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@NoArgsConstructor
@Table(name = "order__cart")
public class OrderCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    @Comment("유저 Id")
    private BigInteger userId;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "total_count")
    private Integer totalCount;


    @Builder
    public OrderCart(Integer id, BigInteger userId, Integer totalPrice, Integer totalCount){
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
    }

    public OrderCart(Integer foodId, BigInteger id, Date serviceDate) {
    }
}