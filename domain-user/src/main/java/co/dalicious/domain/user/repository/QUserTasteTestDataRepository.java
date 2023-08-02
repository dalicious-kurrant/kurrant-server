package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.QUserTasteTestData;
import co.dalicious.domain.user.entity.UserTasteTestData;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static co.dalicious.domain.user.entity.QUserTasteTestData.userTasteTestData;

@Repository
@RequiredArgsConstructor
public class QUserTasteTestDataRepository {

    private final JPAQueryFactory queryFactory;


    public long updateTestData(String foodIds, BigInteger testDataId, Integer page) {

        //page가 null이 아니거나 0이 아닐경우
        if (page != null && page != 0){
            return queryFactory.update(userTasteTestData)
                    .set(userTasteTestData.foodIds, foodIds)
                    .set(userTasteTestData.page, page)
                    .where(userTasteTestData.id.eq(testDataId))
                    .execute();
        }
        // 그 외에는 foodIds만 수정
        return queryFactory.update(userTasteTestData)
                .set(userTasteTestData.foodIds, foodIds)
                .where(userTasteTestData.id.eq(testDataId))
                .execute();
    }

    public List<UserTasteTestData> findAll() {
        return queryFactory.selectFrom(userTasteTestData)
                .fetch();
    }
}
