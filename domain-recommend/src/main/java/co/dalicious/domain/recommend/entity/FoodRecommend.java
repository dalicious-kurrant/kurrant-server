package co.dalicious.domain.recommend.entity;

import co.dalicious.system.converter.IdListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import exception.ApiException;
import exception.CustomException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Table(name = "recommend__food_recommend")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodRecommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Convert(converter = IdListConverter.class)
    @Comment("해당 그룹의 ID")
    private List<BigInteger> groupIds;

    @ElementCollection
    @OrderBy(value = "order DESC")
    @CollectionTable(name = "recommend__food_group_types")
    private List<FoodRecommendTypes> foodRecommendTypes;

    @ElementCollection
    @OrderBy(value = "days DESC")
    @CollectionTable(name = "recommend__food_group_recommend")
    private List<FoodGroupRecommend> foodGroupRecommends;

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

    @Builder
    public FoodRecommend(Collection<BigInteger> groupIds, List<FoodRecommendTypes> foodRecommendTypes, List<FoodGroupRecommend> foodGroupRecommends) {
        if(groupIds == null || groupIds.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "CE400008", "고객사 IDs는 필수 값입니다.");
        }
        this.groupIds = groupIds.stream().toList();
        this.foodRecommendTypes = foodRecommendTypes;
        this.foodGroupRecommends = foodGroupRecommends;
    }

    public void updateFoodRecommend(List<BigInteger> groupIds, List<FoodRecommendTypes> foodRecommendTypes, List<FoodGroupRecommend> foodGroupRecommends) {
        if(!this.groupIds.equals(groupIds)) this.groupIds = groupIds;
        if(!this.foodRecommendTypes.equals(foodRecommendTypes))this.foodRecommendTypes = foodRecommendTypes;
        if(!this.foodGroupRecommends.equals(foodGroupRecommends)) this.foodGroupRecommends = foodGroupRecommends;
    }
}
