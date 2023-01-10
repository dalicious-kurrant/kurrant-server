package co.dalicious.domain.order.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Getter
@Table(name = "order__cart")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("TYPE")
public class OrderCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(name = "user_id")
    @Comment("유저 Id")
    private BigInteger userId;

    @Comment("장바구니 타입, 1:식사/2:마켓")
    @ColumnDefault("1")
    private Integer type;



    @Builder
    public OrderCart(BigInteger id, BigInteger userId){
        this.id = id;
        this.userId = userId;
    }


}