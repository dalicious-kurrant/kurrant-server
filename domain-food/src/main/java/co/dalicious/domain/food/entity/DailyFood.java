package co.dalicious.domain.food.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.converter.DailyFoodStatusConverter;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;


@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__daily_food")
public class DailyFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 일정")
    private DiningType diningType;

    @Column(columnDefinition = "INT DEFAULT 0")
    @Comment("음식 공급 수량")
    private Integer maxCapacity;

    @Column(columnDefinition = "INT DEFAULT 0")
    @Comment("남은 주문 가능 수량")
    private Integer capacity;

    @Convert(converter = DailyFoodStatusConverter.class)
    @Column(name = "e_status")
    @Comment("음식 상태(0. 판매종료 1. 판매중, 2. 주문마감, 3. 일정요청, 4. 일정승인, 5. 등록대기)")
    private DailyFoodStatus dailyFoodStatus;

    @Column(name = "service_date", columnDefinition = "DATE")
    @Comment("배송 날짜")
    private LocalDate serviceDate;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id")
    @Comment("음식")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("스팟")
    private Spot spot;

    public void updateFoodStatus(DailyFoodStatus dailyFoodStatus) {
        this.dailyFoodStatus = dailyFoodStatus;
    }

    public Integer subtractCapacity(Integer foodCount) {
        this.capacity = this.capacity - foodCount;
        return this.capacity;
    }

    public Integer addCapacity(Integer foodCount) {
        this.capacity = this.capacity + foodCount;
        return this.capacity;
    }
}
