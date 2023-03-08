package co.dalicious.domain.recommend.entity;

import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "recommend__user_recommends")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRecommends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Column(name = "user_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("그룹 Id")
    private BigInteger userId;

    @Column(name = "group_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("그룹 Id")
    private BigInteger groupId;

    @Column(name = "makers_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("메이커스 Id")
    private BigInteger makersId;

    @Column(name = "food_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("그룹 Id")
    private BigInteger foodId;

    @Column(name = "service_date")
    @Comment("서비스 제공 날")
    private LocalDate serviceDate;

    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "dining_type")
    @Comment("식단 타입")
    private DiningType DiningType;

    @Column(name = "rank")
    private Integer rank;
}

