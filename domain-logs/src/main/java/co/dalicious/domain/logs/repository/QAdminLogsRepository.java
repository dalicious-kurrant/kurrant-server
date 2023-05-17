package co.dalicious.domain.logs.repository;

import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.logs.entity.AdminLogs;
import co.dalicious.domain.logs.entity.enums.LogType;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static co.dalicious.domain.logs.entity.QAdminLogs.adminLogs;

@RequiredArgsConstructor
@Repository
public class QAdminLogsRepository {
    private final JPAQueryFactory queryFactory;

    public Page<AdminLogs> findAllByFilter(LogType logType, ControllerType controllerType, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (logType != null) {
            whereClause.and(adminLogs.logType.eq(logType));
        }

        if (controllerType != null) {
            whereClause.and(adminLogs.controllerType.eq(controllerType));
        }

        if (startDateTime != null) {
            whereClause.and(adminLogs.createdDateTime.goe(Timestamp.valueOf(startDateTime)));
        }

        if (endDateTime != null) {
            whereClause.and(adminLogs.createdDateTime.loe(Timestamp.valueOf(endDateTime)));
        }

        int offset = limit * (page - 1);

        QueryResults<AdminLogs> results = queryFactory.selectFrom(adminLogs)
                .where(whereClause)
                .orderBy(adminLogs.createdDateTime.desc())
                .limit(limit)
                .offset(offset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
