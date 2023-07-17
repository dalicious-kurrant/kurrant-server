package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.DriverSchedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
