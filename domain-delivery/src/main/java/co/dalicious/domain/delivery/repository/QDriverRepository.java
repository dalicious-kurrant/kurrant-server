package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.DriverSchedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static co.dalicious.domain.delivery.entity.QDriver.driver;
import static co.dalicious.domain.delivery.entity.QDriverSchedule.driverSchedule;

@Repository
@RequiredArgsConstructor
public class QDriverRepository {
    private final JPAQueryFactory queryFactory;

    public List<Driver> findAllByDriverNames(Collection<String> names) {
        return queryFactory.selectFrom(driver)
                .where(driver.name.in(names))
                .fetch();
    }
}