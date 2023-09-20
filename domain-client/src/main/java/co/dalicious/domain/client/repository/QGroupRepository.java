package co.dalicious.domain.client.repository;

import co.dalicious.domain.client.dto.FilterInfo;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.mapper.OpenGroupMapper;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static co.dalicious.domain.client.entity.QCorporation.corporation;
import static co.dalicious.domain.client.entity.QGroup.group;
import static co.dalicious.domain.client.entity.QMealInfo.mealInfo;
import static co.dalicious.domain.client.entity.QOpenGroup.openGroup;
import static co.dalicious.domain.client.entity.QOpenGroupSpot.openGroupSpot;


@Repository
@RequiredArgsConstructor
public class QGroupRepository {

    private final JPAQueryFactory queryFactory;
    private final OpenGroupMapper openGroupMapper;

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

    public Page<Group> findAllExceptForMySpot(BigInteger groupId, Integer limit, Integer page, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if(groupId != null) {
            whereClause.and(group.id.eq(groupId));
        }

        int offset = limit * (page - 1);

        QueryResults<Group> results = queryFactory.selectFrom(group)
                .leftJoin(corporation).on(group.id.eq(corporation.id))
                .leftJoin(openGroup).on(group.id.eq(openGroup.id))
                .where(whereClause, corporation.id.isNotNull().or(openGroup.id.isNotNull()))
                .orderBy(group.id.asc())
                .limit(limit)
                .offset(offset)
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    public List<Group> findAllExceptForMySpot() {
        BooleanBuilder whereClause = new BooleanBuilder();
        return queryFactory.selectFrom(group)
                .leftJoin(corporation).on(group.id.eq(corporation.id))
                .leftJoin(openGroup).on(group.id.eq(openGroup.id))
                .where(whereClause, corporation.id.isNotNull().or(openGroup.id.isNotNull()))
                .orderBy(group.id.asc())
                .fetch();
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

    public List<OpenGroup> findAllOpenGroup() {
        return queryFactory.selectFrom(openGroup)
                .where(openGroup.isActive.isTrue())
                .fetch();
    }

    public List<Group> findAllByIds(List<BigInteger> ids) {
        BooleanBuilder whereCause = new BooleanBuilder();
        if(ids != null && !ids.isEmpty()) {
            whereCause.and(group.id.in(ids));
        }
        return queryFactory.selectFrom(group)
                .where(whereCause)
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

    public List<? extends Group> findGroupByType(GroupDataType groupDataType) {
        BooleanBuilder whereCause = new BooleanBuilder();
        if(groupDataType.equals(GroupDataType.CORPORATION)) {
            whereCause.and(group.instanceOf(Corporation.class));
        }

        if(groupDataType.equals(GroupDataType.OPEN_GROUP)) {
            whereCause.and(group.instanceOf(OpenGroup.class));
        }

        if(groupDataType.equals(GroupDataType.MY_SPOT)) {
            whereCause.and(group.instanceOf(MySpotZone.class));
        }

        return queryFactory.selectFrom(group)
                .where(whereCause)
                .fetch();
    }

    public List<Group> findGroupAndAddressIsNull() {
        return queryFactory.selectFrom(group)
                .where(group.address.location.isNull().or(group.address.address3.isNull()), group.instanceOf(OpenGroup.class).or(group.instanceOf(Corporation.class)))
                .fetch();
    }

    public Page<Group> findOPenGroupByFilter (Boolean isRestriction, List<DiningType> diningType, Pageable pageable, Double let, Double lon) {
        BooleanBuilder whereCause = new BooleanBuilder();

        if (isRestriction != null) {
            whereCause.and(openGroupSpot.isRestriction.eq(isRestriction));
        }
        if (diningType != null) {
            List<Group> openGroupList = queryFactory.select(mealInfo.group)
                    .from(mealInfo)
                    .where(mealInfo.instanceOf(OpenGroupMealInfo.class), mealInfo.diningType.in(diningType))
                    .fetch();
            whereCause.and(group.in(openGroupList));
        }

        QueryResults<Group> resultList = queryFactory.selectFrom(group)
                .leftJoin(mealInfo).on(group.eq(mealInfo.group))
                .leftJoin(openGroupSpot).on(group.eq(openGroupSpot.group))
                .where(group.instanceOf(OpenGroup.class), whereCause, group.isActive.isTrue())
                .orderBy(Expressions.stringTemplate("ST_Distance_Sphere({0}, {1})", Expressions.stringTemplate("POINT({0}, {1})", lon, let), group.address.location).asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        return new PageImpl<>(resultList.getResults(),pageable, resultList.getTotal());
    }

    public Group findGroupByTypeAndId(BigInteger id, GroupDataType groupDataType) {
        BooleanBuilder whereCause =  new BooleanBuilder();

        if(groupDataType.equals(GroupDataType.CORPORATION)) {
            whereCause.and(group.instanceOf(Corporation.class));
        }
        if(groupDataType.equals(GroupDataType.OPEN_GROUP)) {
            whereCause.and(group.instanceOf(OpenGroup.class));
        }

        return queryFactory.selectFrom(group)
                .where(whereCause, group.id.eq(id))
                .fetchOne();
    }

    public Map<BigInteger,String> findGroupNameByIds(Set<BigInteger> groupIds) {
        List<Tuple> result = queryFactory.select(group.id, group.name)
                .from(group)
                .where(group.id.in(groupIds))
                .fetch();

        Map<BigInteger, String> nameMap = new HashMap<>();
        for (Tuple tuple : result) {
            nameMap.put(tuple.get(group.id), tuple.get(group.name));
        }

        return nameMap;
    }

    public List<FilterInfo> getAllIdAndName() {
        return queryFactory.select(Projections.fields(FilterInfo.class, group.id, group.name))
                .from(group)
                .where(group.isActive.isTrue())
                .fetch();
    }
}
