package co.dalicious.domain.order.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "order__cart")
public class OrderCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name= "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(name = "user_id")
    @Comment("유저 Id")
    private BigInteger userId;



    @Builder
    public OrderCart(BigInteger id, BigInteger userId){
        this.id = id;
        this.userId = userId;
    }
}