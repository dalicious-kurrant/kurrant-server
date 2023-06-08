package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.client.entity.QMealInfo.mealInfo;
import static co.dalicious.domain.client.entity.QOpenGroupSpot.openGroupSpot;


@Repository
@RequiredArgsConstructor
public class QGroupRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Group> findAll(BigInteger groupId, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if(groupId != null) {
            whereClause.and(group.id.eq(groupId));
        }

        int offset = limit * (page - 1);

        QueryResults<Group> results = queryFactory.selectFrom(group)
                .where(whereClause)
                .orderBy(group.id.asc())
                .limit(limit)
                .offset(offset)
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public List<Group> findAllByNames(Set<String> groupNames) {
        return queryFactory.selectFrom(group)
                .where(group.name.in(groupNames))
                .fetch();
    }

    public List<Group> findAllByNames(List<String> groupNames) {
        return queryFactory.selectFrom(group)
                .where(group.name.in(groupNames))
                .fetch();
    }

    public List<Group> findAllByIds(Set<BigInteger> ids) {
        return queryFactory.selectFrom(group)
                .where(group.id.in(ids))
                .fetch();
    }

    public List<? extends Group> findAllOpenGroupAndApartment() {
        return queryFactory.selectFrom(group)
                .where(group.instanceOf(Apartment.class).or(group.instanceOf(OpenGroup.class)))
                .fetch();
    }

    public List<Group> findAllByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(group)
                .where(group.id.in(ids))
                .fetch();
    }

//    public void updateSpotDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto, BigInteger groupId) {
//        //담당자 수정
//        if (updateSpotDetailRequestDto.getManagerId() != null){
//            queryFactory.update(group)
//                    .set(group.managerId, updateSpotDetailRequestDto.getManagerId())
//                    .where(group.id.eq(groupId))
//                    .execute();
//        }
//        //메모수정
//        if (updateSpotDetailRequestDto.getMemo() != null){
//            queryFactory.update(group)
//                    .set(group.memo, updateSpotDetailRequestDto.getMemo())
//                    .where(group.id.eq(groupId))
//                    .execute();
//        }
//
//    }

    public BigInteger findById(BigInteger groupId) {
        return queryFactory.select(group.id)
                .from(group)
                .where(group.id.eq(groupId))
                .fetchOne();
    }

    public List<? extends Group> findGroupByType(GroupDataType clientType) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if(clientType.equals(GroupDataType.OPEN_GROUP)) {
            whereCause.and(group.instanceOf(OpenGroup.class));
        }
        if(clientType.equals(GroupDataType.CORPORATION)) {
            whereCause.and(group.instanceOf(Corporation.class));
        }

        return queryFactory.selectFrom(group)
                .where(whereCause)
                .fetch();
    }

    public List<Group> findGroupAndAddressIsNull() {
        return queryFactory.selectFrom(group)
                .where(group.address.location.isNull(), group.instanceOf(OpenGroup.class).or(group.instanceOf(Corporation.class)))
                .fetch();
    }

    public Page<Group> findOPenGroupByFilter (Boolean isRestriction, DiningType diningType, Pageable pageable) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if (isRestriction != null) {
            whereCause.and(openGroupSpot.isRestriction.eq(isRestriction));
        }
        if (diningType != null) {
            List<Group> openGroupList = queryFactory.select(mealInfo.group)
                    .from(mealInfo)
                    .where(mealInfo.instanceOf(OpenGroupMealInfo.class), mealInfo.diningType.eq(diningType))
                    .fetch();
            whereCause.and(group.in(openGroupList));
        }

        QueryResults<Group> resultList = queryFactory.selectFrom(group)
                .leftJoin(mealInfo).on(group.eq(mealInfo.group))
                .leftJoin(openGroupSpot).on(group.id.eq(openGroupSpot.id))
                .where(group.instanceOf(OpenGroup.class), whereCause)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(resultList.getResults(),pageable, resultList.getTotal());
    }

    public Group findGroupByTypeAndId(BigInteger id, GroupDataType clientType) {
        BooleanBuilder whereCause =  new BooleanBuilder();

        if(clientType.equals(GroupDataType.CORPORATION)) {
            whereCause.and(group.instanceOf(Corporation.class));
        }
        if(clientType.equals(GroupDataType.OPEN_GROUP)) {
            whereCause.and(group.instanceOf(OpenGroup.class));
        }

        return queryFactory.selectFrom(group)
                .where(whereCause, group.id.eq(id))
                .fetchOne();
    }
}
