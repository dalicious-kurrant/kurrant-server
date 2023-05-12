package co.dalicious.domain.review.entity;

import co.dalicious.domain.food.entity.Food;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review__keyword")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("키워드 PK")
    private BigInteger id;

    @Comment("키워드 명")
    @Column(name = "name",columnDefinition = "VARCHAR(16)")
    private String name;

    @Comment("키워드 언급 횟수")
    @Column(name = "keyword_count", columnDefinition = "INT")
    private Integer count;

    @Comment("음식 아이디")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", columnDefinition = "BIGINT UNSIGNED")
    private Food food;

    @Builder
    public Keyword(String name, Integer count, Food food) {
        this.name = name;
        this.count = count;
        this.food = food;
    }
}