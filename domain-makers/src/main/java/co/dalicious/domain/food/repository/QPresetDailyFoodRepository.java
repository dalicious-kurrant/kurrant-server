package co.dalicious.domain.food.repository;

import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.util.QuerydslDateFormatUtils;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.dalicious.domain.client.entity.QEmployee.employee;
import static co.dalicious.domain.food.entity.QPresetDailyFood.presetDailyFood;
import static co.dalicious.domain.food.entity.QPresetMakersDailyFood.presetMakersDailyFood;

@Repository
@RequiredArgsConstructor
public class QPresetDailyFoodRepository{
    private final JPAQueryFactory queryFactory;

    public PresetDailyFood findById(BigInteger id){
        return queryFactory.selectFrom(presetDailyFood)
                .where(presetDailyFood.id.eq(id))
                .fetchOne();
    }

    public Page<PresetDailyFood> findAllByCreatedDate(Pageable pageable, Integer size, Integer page) {
        StringTemplate formattedDate = QuerydslDateFormatUtils.getStringTemplateByTimestamp(presetDailyFood.createdDateTime);

        String dates = queryFactory.select(formattedDate).from(presetDailyFood)
                .groupBy(formattedDate)
                .orderBy(formattedDate.desc())
                .limit(1)
                .fetchOne();
        System.out.println("dates = " + dates);

        if (dates != null) {
            LocalDate date = DateUtils.stringToDate(dates);
            LocalDateTime startDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);
            int itemLimit = size * page;
            int itemOffset = size * (page - 1);

            QueryResults<PresetDailyFood> results = queryFactory.selectFrom(presetDailyFood)
                    .where(presetDailyFood.createdDateTime.between(Timestamp.valueOf(startDate), Timestamp.valueOf(endDate)))
                    .limit(itemLimit)
                    .offset(itemOffset)
                    .fetchResults();

            return new PageImpl<>(results.getResults(), pageable, results.getTotal());
        }

        return null;
    }

//    public static StringTemplate getStringTemplate() {
//        StringTemplate formattedDate = Expressions.stringTemplate(
//                "DATE_FORMAT({0}, {1})"
//                , presetDailyFood.createdDateTime
//                , ConstantImpl.create("%Y-%m-%d"));
//        return formattedDate;
//    }
}
