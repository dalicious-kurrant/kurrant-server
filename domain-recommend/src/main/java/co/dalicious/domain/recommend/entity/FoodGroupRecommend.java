package co.dalicious.domain.recommend.entity;

import co.dalicious.system.converter.IdListConverter;
import co.dalicious.system.enums.Days;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class FoodGroupRecommend {
    @Comment("요일")
    @Column(unique = true)
    private Days days;

    @Comment("요일에 해당하는 음식 그룹 IDs")
    @Convert(converter = IdListConverter.class)
    // 음식 그룹 삭제시, 추천에서도 삭제
    private List<BigInteger> foodGroups;

    public FoodGroupRecommend(Days days, List<BigInteger> foodGroups) {
        this.days = days;
        this.foodGroups = foodGroups;
    }
}
