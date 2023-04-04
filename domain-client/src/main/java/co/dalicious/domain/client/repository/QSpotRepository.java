package co.dalicious.domain.client.repository;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.client.entity.QSpot.spot;

@Repository
@RequiredArgsConstructor
public class QSpotRepository {

    private final JPAQueryFactory queryFactory;

    public List<BigInteger> findAllByGroupId(BigInteger corporationId) {
        return queryFactory
                .select(spot.id)
                .from(spot)
                .where(spot.group.id.eq(corporationId))
                .fetch();
    }


    public long deleteSpots(List<BigInteger> spotIdList) {
        return queryFactory.update(spot)
                .set(spot.status, SpotStatus.INACTIVE)
                .where(spot.id.in(spotIdList))
                .execute();
    }

    public List<Spot> findAllByStatus(Integer status) {
        if (status == null) {
            return queryFactory.selectFrom(spot)
                    .fetch();
        } else if (status == 1) {
            return queryFactory.selectFrom(spot)
                    .where(spot.status.eq(SpotStatus.ACTIVE))
                    .fetch();
        } else if (status == 0) {
            return queryFactory.selectFrom(spot)
                    .where(spot.status.eq(SpotStatus.INACTIVE))
                    .fetch();
        }
        return null;
    }

    public List<Spot> findAllByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(spot)
                .where(spot.id.in(ids))
                .fetch();
    }

    public List<Spot> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(spot)
                .where(spot.id.in(ids))
                .fetch();
    }

    public List<Spot> findAllByGroupIds(List<BigInteger> groupIds) {
        return queryFactory.selectFrom(spot)
                .where(spot.group.id.in(groupIds))
                .fetch();
    }

    public void updateSpotDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException {

        //스팟이름
        if (updateSpotDetailRequestDto.getSpotName() != null && !updateSpotDetailRequestDto.getSpotName().equals("")){
            queryFactory.update(spot)
                    .set(spot.name, updateSpotDetailRequestDto.getSpotName())
                    .where(spot.id.eq(updateSpotDetailRequestDto.getSpotId()))
                    .execute();
        }

        //우편번호
        if (updateSpotDetailRequestDto.getZipCode() != null && !updateSpotDetailRequestDto.getZipCode().equals("")){
            queryFactory.update(spot)
                    .set(spot.address.zipCode, updateSpotDetailRequestDto.getZipCode())
                    .where(spot.id.eq(updateSpotDetailRequestDto.getSpotId()))
                    .execute();
        }

        //기본주소
        if (updateSpotDetailRequestDto.getAddress1() != null && !updateSpotDetailRequestDto.getAddress1().equals("")){
            queryFactory.update(spot)
                    .set(spot.address.address1, updateSpotDetailRequestDto.getAddress1())
                    .where(spot.id.eq(updateSpotDetailRequestDto.getSpotId()))
                    .execute();
        }

        //상세주소
        if (updateSpotDetailRequestDto.getAddress2() != null && !updateSpotDetailRequestDto.getAddress2().equals("")){
            queryFactory.update(spot)
                    .set(spot.address.address2, updateSpotDetailRequestDto.getAddress2())
                    .where(spot.id.eq(updateSpotDetailRequestDto.getSpotId()))
                    .execute();
        }

        //위치
        if (updateSpotDetailRequestDto.getLocation() != null && !updateSpotDetailRequestDto.getLocation().equals("")){
            queryFactory.update(spot)
                    .set(spot.address.location, Address.createPoint(updateSpotDetailRequestDto.getLocation()))
                    .where(spot.id.eq(updateSpotDetailRequestDto.getSpotId()))
                    .execute();
        }

        //메모
        if (updateSpotDetailRequestDto.getMemo() != null && !updateSpotDetailRequestDto.getMemo().equals("")){
            queryFactory.update(spot)
                    .set(spot.memo, updateSpotDetailRequestDto.getMemo())
                    .where(spot.id.eq(updateSpotDetailRequestDto.getSpotId()))
                    .execute();
        }


    }

    public BigInteger getGroupId(BigInteger spotId) {
        return queryFactory.select(spot.group.id)
                .from(spot)
                .where(spot.id.eq(spotId))
                .fetchOne();
    }
}
