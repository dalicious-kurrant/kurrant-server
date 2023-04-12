package co.dalicious.domain.user.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__taste_test_data")
public class UserTasteTestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("PK")
    private BigInteger id;

    @Column(name="food_id_list", columnDefinition = "VARCHAR(255)")
    @Comment("목록에 보여줄 foodId")
    private String foodIds;

    @Column(name="page", columnDefinition = "INT")
    @Comment("보여줄 페이지 번호")
    private Integer page;

    @Builder
    public UserTasteTestData(String foodIds, Integer page){
        this.foodIds = foodIds;
        this.page = page;
    }

}
