package co.dalicious.domain.food.entity;

import co.dalicious.domain.makers.entity.Makers;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
    private Integer id;

    @Column(name = "name")
    @Comment("식품 이름")
    private String name;

    @Column(name = "price")
    @Comment("가격")
    private Integer price;

    @Column(name = "img")
    @Comment("이미지 경로")
    private String img;

    @Column(name = "discounted_rate")
    @Comment("할인율")
    private Double discountedRate;

    @ManyToOne
    @JoinColumn(name = "makers_id")
    @Comment("메이커스 ID")
    private Makers makers;

    @Column(name = "description")
    @Comment("설명")
    private String description;

    @Builder
    Food(Integer id, String name, Integer price, String img, Makers makers, String description, Double discountedRate){
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.makers = makers;
        this.description = description;
        this.discountedRate = discountedRate;
    }
}
