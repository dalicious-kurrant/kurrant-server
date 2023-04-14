package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.Corporation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static co.dalicious.domain.client.entity.QCorporation.corporation;

@Repository
@RequiredArgsConstructor
public class QCorporationRepository {

    private final JPAQueryFactory queryFactory;


    public BigInteger findOneByCode(String code) {
        return queryFactory.select(corporation.id)
                .from(corporation)
                .where(corporation.code.eq(code))
                .fetchOne();
    }

    public Corporation findEntityByCode(String code) {
        return queryFactory.selectFrom(corporation)
                .where(corporation.code.eq(code))
                .fetchOne();
    }

    public void updateSpotDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto, BigInteger groupId) {

        queryFactory.update(corporation)
                .set(corporation.isSetting, updateSpotDetailRequestDto.getIsSetting())
                .set(corporation.isGarbage, updateSpotDetailRequestDto.getIsGarbage())
                .set(corporation.isHotStorage, updateSpotDetailRequestDto.getIsHotStorage())
                .set(corporation.minimumSpend, updateSpotDetailRequestDto.getMinPrice())
                .set(corporation.maximumSpend, updateSpotDetailRequestDto.getMaxPrice())
                .set(corporation.isMembershipSupport, updateSpotDetailRequestDto.getIsMembershipSupport())
                .where(corporation.id.eq(groupId))
                .execute();

    }
}
