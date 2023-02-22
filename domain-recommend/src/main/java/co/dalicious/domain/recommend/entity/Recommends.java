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
@Table(name = "Recommends__Recommends")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Column(name = "group_id")
    @Comment("그룹 Id")
    private BigInteger groupId;

    @Column(name = "makers_id")
    @Comment("메이커스 Id")
    private BigInteger makersId;

    @Column(name = "service_date")
    @Comment("서비스 제공 날")
    private LocalDate serviceDate;

    @Column(name = "is_reject")
    @Comment("메이커스의 거절 여부")
    private Boolean isReject;

    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "dining_type")
    @Comment("식단 타입")
    private DiningType DiningType;
}

