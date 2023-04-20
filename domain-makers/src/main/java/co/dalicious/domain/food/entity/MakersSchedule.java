package co.dalicious.domain.food.entity;


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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__makers_schedule")
public class MakersSchedule {
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

    public MakersSchedule(LocalDate serviceDate, DiningType diningType, Integer capacity, Makers makers) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.capacity = capacity;
        this.makers = makers;
    }
}
