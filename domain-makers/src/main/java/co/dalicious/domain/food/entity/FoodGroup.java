package co.dalicious.domain.food.entity;

import co.dalicious.system.converter.IntegerToStringConverter;
import co.dalicious.system.enums.FoodTag;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__food_group")
public class FoodGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("음식 그룹 id")
    private BigInteger id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "food__food")
    @Comment("메이커스 pk")
    private Makers makers;


    @OneToMany(mappedBy = "foodGroup", fetch = FetchType.LAZY)
    @JsonBackReference(value = "food__food_fk")
    @Comment("음식 그룹에 속한 음식 pks")
    private List<Food> foods;

    @Column(unique = true)
    @Comment("식품 그룹 이름")
    private String name;

    @Convert(converter = IntegerToStringConverter.class)
    @Comment("동일 날짜 동시 추천 가능 여부, 숫자가 같은 그룹끼리는 같은 날 추천 가능")
    private List<Integer> groupNumbers;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    public Integer totalFoodCount() {
        return this.foods.size();
    }

    public Integer foodTagCount(FoodTag foodTag) {
        return Math.toIntExact(foods.stream()
                .filter(v -> v.getFoodTags().contains(foodTag))
                .count());
    }

    public void updateFoodGroup(String name, List<Integer> groupNumbers) {
        this.name = name;
        this.groupNumbers = groupNumbers;
    }

    @Builder
    public FoodGroup(Makers makers, String name, List<Integer> groupNumbers) {
        this.makers = makers;
        this.name = name;
        this.groupNumbers = groupNumbers;
    }
}
