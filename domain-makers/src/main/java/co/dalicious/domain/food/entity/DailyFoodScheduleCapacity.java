package co.dalicious.domain.food.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Schema(description = "식사 일정별 가능 수량. DailyFood가 판매중일 때 자동생성")
public class DailyFoodScheduleCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Comment("일일 식사 일정별 총 가능 수량")
    private Integer maxCapacity;

    @Comment("판매 개수")
    private Integer count;
    
}
