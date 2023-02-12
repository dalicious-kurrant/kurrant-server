package co.dalicious.domain.food.entity;


import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.converter.DiningTypeConverter;
import co.dalicious.system.util.enums.DiningType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Comment("메이커스 서비스일")
    private LocalDate serviceDate;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사타입")
    private DiningType diningType;

    @Comment("식사 일정과 날짜별 가능한 주문 수량")
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("메이커스")
    private Makers makers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("음식")
    private Food food;
}