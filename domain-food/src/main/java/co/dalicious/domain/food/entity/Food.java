package co.dalicious.domain.food.entity;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.enums.Spicy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Column(name = "name")
    @Comment("식품 이름")
    private String name;

    @Column(name = "price")
    @Comment("가격")
    private BigDecimal price;

    @Embedded
    @Comment("이미지 경로")
    private Image image;

    @OneToMany(mappedBy = "food")
    @JsonBackReference(value = "food_fk")
    private List<FoodDiscountPolicy> foodDiscountPolicyList;

    @Column(name = "spicy")
    @Comment("맵기정도")
    private Spicy spicy;

    @ManyToOne(fetch = FetchType.EAGER ,optional = false)
    @JoinColumn(name = "makers_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("메이커스 ID")
    private Makers makers;

    @Column(name = "description")
    @Comment("설명")
    private String description;

}
