package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.DriverSchedule;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static co.dalicious.domain.delivery.entity.QDriverSchedule.driverSchedule;
@Repository
@RequiredArgsConstructor
public class QDriverScheduleRepository {
    private final JPAQueryFactory queryFactory;

    public List<DriverSchedule> findByPeriod(LocalDate startDate, LocalDate endDate) {
        return queryFactory.selectFrom(driverSchedule)
                .where(driverSchedule.deliveryDate.goe(startDate), driverSchedule.deliveryDate.loe(endDate))
                .fetch();
    }

    public DriverSchedule find(LocalDate deliveryDate, DiningType diningType, LocalTime deliveryTime, String driverName) {
        return queryFactory.selectFrom(driverSchedule)
                .where(driverSchedule.deliveryDate.eq(deliveryDate),
                        driverSchedule.diningType.eq(diningType),
                        driverSchedule.deliveryTime.eq(deliveryTime),
                        driverSchedule.driver.name.eq(driverName))
                .fetchOne();
    }
}
