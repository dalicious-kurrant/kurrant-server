package co.dalicious.domain.food.entity;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.enums.FoodStatus;
import co.dalicious.system.util.converter.DiningTypeConverter;
import co.dalicious.system.util.converter.FoodStatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;


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
    @Column(name = "dining_type")
    private DiningType diningType;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDate updated;

    @Convert(converter = FoodStatusConverter.class)
    @Column(name = "e_status")
    private FoodStatus foodStatus;

    @Column(name = "service_date", columnDefinition = "DATE")
    private LocalDate serviceDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @Comment("스팟")
    private Spot spot;

}
