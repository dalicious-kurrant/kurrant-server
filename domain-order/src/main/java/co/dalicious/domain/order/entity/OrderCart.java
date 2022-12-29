package co.dalicious.domain.order.entity;

import co.dalicious.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "order__cart")
public class OrderCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    @Comment("유저 Id")
    private BigInteger userId;



    @Builder
    public OrderCart(Integer id, BigInteger userId){
        this.id = id;
        this.userId = userId;
    }
}