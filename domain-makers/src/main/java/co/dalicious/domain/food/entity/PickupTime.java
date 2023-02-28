package co.dalicious.domain.food.entity;

import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.time.LocalTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__pickup_time")
public class PickupTime {
    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 타입")
    private DiningType diningType;

    @Comment("메이커스 픽업 시간")
    private LocalTime pickupTime;
}
