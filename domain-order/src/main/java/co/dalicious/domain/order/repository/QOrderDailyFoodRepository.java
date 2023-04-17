package co.dalicious.domain.order.repository;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.order.dto.CapacityDto;
import co.dalicious.domain.order.dto.ServiceDiningDto;
import co.dalicious.domain.order.dto.ServiceDateBy;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.PeriodDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static co.dalicious.domain.food.entity.QDailyFood.dailyFood;
import static co.dalicious.domain.food.entity.QFood.food;
import static co.dalicious.domain.food.entity.QMakers.makers;
import static co.dalicious.domain.order.entity.QOrderDailyFood.orderDailyFood;
import static co.dalicious.domain.order.entity.QOrderItemDailyFood.orderItemDailyFood;



@Repository
@RequiredArgsConstructor
public class QOrderDailyFoodRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public List<OrderItemDailyFood> findExtraOrdersByManagerId(List<BigInteger> userIds, LocalDate startDate, LocalDate endDate) {
        BooleanExpression whereClause = orderDailyFood.user.id.in(userIds);
        if (startDate != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.serviceDate.loe(endDate));
        }

        whereClause = whereClause.and(orderDailyFood.orderType.eq(OrderType.DAILYFOOD));
        whereClause = whereClause.and(orderDailyFood.paymentType.eq(PaymentType.SUPPORT_PRICE));

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.desc())
                .fetch();
    }
    public List<OrderItemDailyFood> findByUserAndServiceDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()),
                        orderItemDailyFood.orderItemDailyFoodGroup.serviceDate.between(startDate, endDate))
                .fetch();
    }

    public List<OrderItemDailyFood> findByServiceDate(LocalDate today) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.serviceDate.eq(today))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllWhichGetMembershipBenefit(User user, LocalDateTime now, LocalDateTime threeMonthAgo) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.createdDateTime.between(Timestamp.valueOf(threeMonthAgo), Timestamp.valueOf(now)),
                        orderItemDailyFood.orderStatus.eq(OrderStatus.COMPLETED),
                        orderItemDailyFood.membershipDiscountRate.gt(0))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllMealScheduleByUser(User user) {
        return queryFactory
                .selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.dailyFood.serviceDate.goe(LocalDate.now()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
    }

    public List<OrderItemDailyFood> findAllByGroupFilter(LocalDate startDate, LocalDate endDate, Group group, List<BigInteger> spotIds, Integer diningTypeCode, BigInteger userId, Makers selectedMakers) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (startDate != null) {
            whereClause.and(orderItemDailyFood.dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause.and(orderItemDailyFood.dailyFood.serviceDate.loe(endDate));
        }

        if (diningTypeCode != null) {
            whereClause.and(orderItemDailyFood.dailyFood.diningType.eq(DiningType.ofCode(diningTypeCode)));
        }

        if (userId != null) {
            whereClause.and(orderItemDailyFood.order.user.id.eq(userId));
        }

        if (spotIds != null && !spotIds.isEmpty()) {
            whereClause.and(orderDailyFood.spot.id.in(spotIds));
        }

        if (selectedMakers != null) {
            whereClause.and(makers.eq(selectedMakers));
        }

        if (group != null) {
            whereClause.and(orderItemDailyFood.dailyFood.group.eq(group));
        }

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.desc())
                .fetch();
    }

    public List<OrderItemDailyFood> findAllGroupOrderByFilter(Group group, LocalDate startDate, LocalDate endDate, List<BigInteger> spotIds, Integer diningTypeCode, BigInteger userId) {
        BooleanExpression whereClause = orderItemDailyFood.dailyFood.group.eq(group);

        if (startDate != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.serviceDate.loe(endDate));
        }

        if (diningTypeCode != null) {
            whereClause = whereClause.and(orderItemDailyFood.dailyFood.diningType.eq(DiningType.ofCode(diningTypeCode)));
        }

        if (userId != null) {
            whereClause = whereClause.and(orderItemDailyFood.order.user.id.eq(userId));
        }

        if (spotIds != null && !spotIds.isEmpty()) {
            whereClause = whereClause.and(orderDailyFood.spot.id.in(spotIds));
        }

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.desc())
                .fetch();
    }

    public List<OrderItemDailyFood> findAllByMakersFilter(LocalDate startDate, LocalDate endDate, Makers selectedMakers, List<Integer> diningTypeCodes) {
        BooleanExpression whereClause = makers.eq(selectedMakers);

        if (startDate != null) {
            whereClause = whereClause.and(dailyFood.serviceDate.goe(startDate));
        }

        if (endDate != null) {
            whereClause = whereClause.and(dailyFood.serviceDate.loe(endDate));
        }

        if (diningTypeCodes != null && !diningTypeCodes.isEmpty()) {
            List<DiningType> diningTypes = new ArrayList<>();
            for (Integer diningType : diningTypeCodes) {
                diningTypes.add(DiningType.ofCode(diningType));
            }
            whereClause = whereClause.and(dailyFood.diningType.in(diningTypes));
        }

        whereClause = whereClause.and(orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()));

        return queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderDailyFood).on(orderItemDailyFood.order.id.eq(orderDailyFood.id))
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(whereClause)
                .orderBy(orderItemDailyFood.dailyFood.serviceDate.asc())
                .fetch();
    }

    public Integer getFoodCount(DailyFood selectedDailyFood) {
        int count = 0;
        List<OrderItemDailyFood> orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.food.eq(selectedDailyFood.getFood()),
                        orderItemDailyFood.dailyFood.serviceDate.eq(selectedDailyFood.getServiceDate()),
                        orderItemDailyFood.dailyFood.diningType.eq(selectedDailyFood.getDiningType()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
        for (OrderItemDailyFood itemDailyFood : orderItemDailyFoods) {
            count += itemDailyFood.getCount();
        }
        return count;
    }

    public Integer getMakersCount(DailyFood selectedDailyFood) {
        int count = 0;
        List<OrderItemDailyFood> orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(makers.eq(selectedDailyFood.getFood().getMakers()),
                        dailyFood.serviceDate.eq(selectedDailyFood.getServiceDate()),
                        dailyFood.diningType.eq(selectedDailyFood.getDiningType()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
        for (OrderItemDailyFood itemDailyFood : orderItemDailyFoods) {
            count += itemDailyFood.getCount();
        }
        return count;
    }

    public ServiceDateBy.MakersAndFood getMakersCounts(List<DailyFood> dailyFoods) {
        Map<ServiceDateBy.Makers, Integer> makersIntegerMap = new HashMap<>();
        Map<ServiceDateBy.Food, Integer> foodIntegerMap = new HashMap<>();
        Set<ServiceDiningDto> serviceDiningDtos = new HashSet<>();
        Set<Makers> makersSet = new HashSet<>();

        for (DailyFood dailyFood : dailyFoods) {
            ServiceDiningDto serviceDiningDto = new ServiceDiningDto(dailyFood);
            serviceDiningDtos.add(serviceDiningDto);
            makersSet.add(dailyFood.getFood().getMakers());
        }

        // 기간 구하기
        PeriodDto periodDto = UserSupportPriceUtil.getEarliestAndLatestServiceDate(serviceDiningDtos);

        List<OrderItemDailyFood> orderItemDailyFoods = queryFactory.selectFrom(orderItemDailyFood)
                .innerJoin(orderItemDailyFood.dailyFood, dailyFood)
                .innerJoin(dailyFood.food, food)
                .innerJoin(food.makers, makers)
                .where(makers.in(makersSet),
                        dailyFood.serviceDate.goe(periodDto.getStartDate()),
                        dailyFood.serviceDate.loe(periodDto.getEndDate()),
                        orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();

        MultiValueMap<ServiceDateBy.Makers, OrderItemDailyFood> makersOrderMap = new LinkedMultiValueMap<>();
        MultiValueMap<ServiceDateBy.Food, OrderItemDailyFood> foodOrderMap = new LinkedMultiValueMap<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            ServiceDateBy.Makers makersDto = new ServiceDateBy.Makers();
            makersDto.setDiningType(orderItemDailyFood.getDailyFood().getDiningType());
            makersDto.setServiceDate(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate());
            makersDto.setMakers(orderItemDailyFood.getDailyFood().getFood().getMakers());
            makersOrderMap.add(makersDto, orderItemDailyFood);

            ServiceDateBy.Food foodDto = new ServiceDateBy.Food();
            foodDto.setDiningType(orderItemDailyFood.getDailyFood().getDiningType());
            foodDto.setServiceDate(orderItemDailyFood.getOrderItemDailyFoodGroup().getServiceDate());
            foodDto.setFood(orderItemDailyFood.getDailyFood().getFood());
            foodOrderMap.add(foodDto, orderItemDailyFood);
        }

        for (ServiceDateBy.Makers makers1 : makersOrderMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoodList = makersOrderMap.get(makers1);
            Integer count = 0;
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
                count += orderItemDailyFood.getCount();
            }
            makersIntegerMap.put(makers1, count);
        }

        for (ServiceDateBy.Food food1 : foodOrderMap.keySet()) {
            List<OrderItemDailyFood> orderItemDailyFoodList = foodOrderMap.get(food1);
            Integer count = 0;
            for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
                count += orderItemDailyFood.getCount();
            }
            foodIntegerMap.put(food1, count);
        }
        ServiceDateBy.MakersAndFood makersAndFood = new ServiceDateBy.MakersAndFood();
        makersAndFood.setMakersCountMap(makersIntegerMap);
        makersAndFood.setFoodCountMap(foodIntegerMap);

        return makersAndFood;
    }

    public List<CapacityDto.MakersCapacity> getMakersCounts(List<DailyFood> selectedDailyFoods, Set<Makers> makers) {
        List<CapacityDto.MakersCapacity> makersCapacities = new ArrayList<>();
        List<Makers> selectedMakers = new ArrayList<>();
        List<LocalDate> selectedServiceDate = new ArrayList<>();
        List<DiningType> selectedDiningTypes = new ArrayList<>();

        for (DailyFood selectedDailyFood : selectedDailyFoods) {
            selectedMakers.add(selectedDailyFood.getFood().getMakers());
            selectedServiceDate.add(selectedDailyFood.getServiceDate());
            selectedDiningTypes.add(selectedDailyFood.getDiningType());
        }

        String nativeQuery = "SELECT f.service_date, f.e_dining_type, makers_id, sum(oid.count) " +
                "FROM order__order_item_dailyfood oid " +
                "LEFT JOIN food__daily_food f ON f.id = oid.daily_food_id " +
                "LEFT JOIN food__food ff ON ff.id = f.food_id " +
                "LEFT JOIN order__order_item oi ON oi.id = oid.id " +
                "WHERE f.service_date IN :serviceDates " +
                "AND f.e_dining_type IN :diningTypes " +
                "AND makers_id IN :makersIds " +
                "AND oi.e_order_status IN :orderStatus " +
                "GROUP BY f.service_date, f.e_dining_type, makers_id";

        @SuppressWarnings("unchecked")
        List<Object[]> result = entityManager.createNativeQuery(nativeQuery)
                .setParameter("serviceDates", selectedServiceDate)
                .setParameter("diningTypes", selectedDiningTypes.stream().map(DiningType::getCode).toList())
                .setParameter("makersIds", selectedMakers.stream().map(Makers::getId).toList())
                .setParameter("orderStatus", OrderStatus.completePayment().stream().map(OrderStatus::getCode).toList())
                .getResultList();

        for (Object[] tuple : result) {
            LocalDate serviceDate = ((Date) tuple[0]).toLocalDate();
            DiningType diningType = DiningType.values()[(int) tuple[1]];
            Makers maker = makers.stream()
                    .filter(v -> v.getId().equals(tuple[2]))
                    .findAny().orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));
            Integer capacity = ((Number) tuple[3]).intValue();
            makersCapacities.add(new CapacityDto.MakersCapacity(serviceDate, diningType, maker, capacity));
        }

        return makersCapacities;
    }


    public List<OrderItemDailyFood> findAllByIds(List<BigInteger> ids) {
        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.id.in(ids))
                .fetch();
    }

    public List<OrderItemDailyFood> findByDailyFoodAndOrderStatus(List<DailyFood> dailyFoodList) {
        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.dailyFood.in(dailyFoodList), orderItemDailyFood.orderStatus.in(OrderStatus.completePayment()))
                .fetch();
    }

    public OrderItemDailyFood findByUserAndId(User user, BigInteger id) {
        return queryFactory.selectFrom(orderItemDailyFood)
                .where(orderItemDailyFood.order.user.eq(user),
                        orderItemDailyFood.id.eq(id),
                        orderItemDailyFood.orderStatus.eq(OrderStatus.DELIVERED))
                .fetchOne();
    }



}
