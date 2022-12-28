package co.dalicious.domain.food.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "food__origin")
public class Origin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("ID")
    private Integer id;

    @Column(name = "origin_name")
    @Comment("품목 이름")
    private String name;

    @Column(name = "origin_from")
    @Comment("원산지")
    private String from;

    @ManyToOne
    @JoinColumn(name = "food_id")
    @Comment("식품 ID")
    private Food food;
}
