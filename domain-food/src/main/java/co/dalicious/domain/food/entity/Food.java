package co.dalicious.domain.food.entity;

import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.Spicy;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "food__food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("ID")
    private BigInteger id;

    @Column(name = "name")
    @Comment("식품 이름")
    private String name;

    @Column(name = "price")
    @Comment("가격")
    private BigDecimal price;

    @Column(name = "img")
    @Comment("이미지 경로")
    private String img;

    @Column(name = "discounted_rate")
    @Comment("할인율")
    private BigDecimal discountedRate;

    @Column(name = "e_spicy")
    @Comment("맵기정도")
    private Spicy spicy;

    @ManyToOne
    @JoinColumn(name = "makers_id")
    @Comment("메이커스 ID")
    private Makers makers;

    @Column(name = "description")
    @Comment("설명")
    @Lob
    private String description;

    @Builder
    Food(BigInteger id, String name, BigDecimal price, String img, Makers makers, String description, BigDecimal discountedRate, Spicy spicy){
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.spicy = spicy;
        this.makers = makers;
        this.description = description;
        this.discountedRate = discountedRate;
    }
}
