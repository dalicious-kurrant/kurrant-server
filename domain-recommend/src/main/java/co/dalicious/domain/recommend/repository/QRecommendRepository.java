package co.dalicious.domain.recommend.repository;

import co.dalicious.domain.recommend.entity.Recommends;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static co.dalicious.domain.recommend.entity.QRecommends.recommends;

@Repository
@RequiredArgsConstructor
public class QRecommendRepository {
    private final JPAQueryFactory queryFactory;

    public Page<Recommends> getRecommendPresetSchedule(Pageable pageable, Integer size, Integer page, String startDate){
        // start date 기준으로 14일
        LocalDate start = DateUtils.stringToDate(startDate);
        LocalDate end = start.plusDays(14);

        // page
        int itemLimit = size * page;
        int itemOffset = size * (page - 1);

        QueryResults<Recommends> results = queryFactory.selectFrom(recommends)
                .where(recommends.serviceDate.between(start, end),
                        recommends.isReject.ne(1))
                .limit(itemLimit)
                .offset(itemOffset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
