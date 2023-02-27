package co.dalicious.domain.recommend.repository;

import co.dalicious.domain.recommend.entity.GroupRecommends;
import co.dalicious.system.util.DateUtils;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;

import static co.dalicious.domain.recommend.entity.QGroupRecommends.groupRecommends;


@Repository
@RequiredArgsConstructor
public class QGroupRecommendRepository {
    private final JPAQueryFactory queryFactory;

    public Page<GroupRecommends> getRecommendPresetSchedule(Pageable pageable, Integer size, Integer page, String startDate, String endDate){
        // start date 기준으로 14일
        LocalDate start = DateUtils.stringToDate(startDate);
        LocalDate end = DateUtils.stringToDate(endDate);

        // page
        int itemLimit = size * page;
        int itemOffset = size * (page - 1);

        QueryResults<GroupRecommends> results = queryFactory.selectFrom(groupRecommends)
                .where(groupRecommends.serviceDate.between(start, end),
                        groupRecommends.isReject.ne(1))
                .limit(itemLimit)
                .offset(itemOffset)
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public void updateIsReject(BigInteger makersId, LocalDate serviceDate) {
        queryFactory.update(groupRecommends)
                        .where(groupRecommends.makersId.eq(makersId),
                        groupRecommends.serviceDate.eq(serviceDate))
                        .set(groupRecommends.isReject, 1)
                        .execute();
    }
}
