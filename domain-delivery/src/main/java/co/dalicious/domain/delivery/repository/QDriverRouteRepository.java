package co.dalicious.domain.delivery.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.delivery.entity.Driver;
import co.dalicious.domain.delivery.entity.DriverRoute;
import co.dalicious.domain.delivery.entity.DriverSchedule;
import co.dalicious.system.enums.DiningType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static co.dalicious.domain.delivery.entity.QDriver.driver;
import static co.dalicious.domain.delivery.entity.QDriverRoute.driverRoute;

@Repository
@RequiredArgsConstructor
public class QDriverRouteRepository {
    private final JPAQueryFactory queryFactory;

    public List<DriverRoute> findAllByDriverRoute(DriverRoute selectedDriverRoute) {
        return queryFactory.selectFrom(driverRoute)
                .where(driverRoute.driverSchedule.deliveryDate.eq(selectedDriverRoute.getDriverSchedule().getDeliveryDate()),
                        driverRoute.driverSchedule.diningType.eq(selectedDriverRoute.getDriverSchedule().getDiningType()),
                        driverRoute.driverSchedule.deliveryTime.eq(selectedDriverRoute.getDriverSchedule().getDeliveryTime()),
                        driverRoute.group.eq(selectedDriverRoute.getGroup()),
                        driverRoute.makers.eq(selectedDriverRoute.getMakers()))
                .fetch();
    }

}
