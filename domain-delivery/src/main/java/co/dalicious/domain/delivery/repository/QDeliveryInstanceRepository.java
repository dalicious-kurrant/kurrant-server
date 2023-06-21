package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.delivery.entity.DeliveryInstance;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static co.dalicious.domain.delivery.entity.QDeliveryInstance.deliveryInstance;

@Repository
@RequiredArgsConstructor
public class QDeliveryInstanceRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<DeliveryInstance> findBy(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, Makers makers, Spot spot) {
        return Optional.ofNullable(queryFactory.selectFrom(deliveryInstance)
                .where(deliveryInstance.deliveryTime.eq(deliveryTime),
                        deliveryInstance.diningType.eq(diningType),
                        deliveryInstance.serviceDate.eq(serviceDate),
                        deliveryInstance.makers.eq(makers),
                        deliveryInstance.spot.eq(spot))
                .fetchOne());
    }

    public List<DeliveryInstance> findByFilter(LocalDate startDate, LocalDate endDate, List<DiningType> diningTypes, Makers makers) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (startDate != null) {
            whereClause.and(deliveryInstance.serviceDate.goe(startDate));
        }
        if (endDate != null) {
            whereClause.and(deliveryInstance.serviceDate.loe(endDate));
        }
        if (diningTypes != null && !diningTypes.isEmpty()) {
            whereClause.and(deliveryInstance.diningType.in(diningTypes));
        }
        return queryFactory.selectFrom(deliveryInstance)
                .where(deliveryInstance.makers.eq(makers),
                        whereClause)
                .fetch();
    }

    public Integer getMaxOrderNumber(LocalDate serviceDate, DiningType diningType, LocalTime deliveryTime, Makers makers) {
        Integer maxOrderNumber = queryFactory.select(deliveryInstance.orderNumber.max())
                .from(deliveryInstance)
                .where(deliveryInstance.serviceDate.eq(serviceDate),
                        deliveryInstance.diningType.eq(diningType),
                        deliveryInstance.deliveryTime.eq(deliveryTime),
                        deliveryInstance.makers.eq(makers))
                .fetchOne();

        return Objects.requireNonNullElse(maxOrderNumber, 0);
    }

    public List<LocalTime> getTodayDeliveryTimes() {
        return queryFactory.select(deliveryInstance.deliveryTime)
                .from(deliveryInstance)
                .where(deliveryInstance.serviceDate.eq(LocalDate.now()))
                .distinct()
                .fetch();
    }
}
