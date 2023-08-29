package co.kurrant.app.public_api.mapper.food;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.SupportType;
import co.dalicious.domain.food.dto.DailyFoodDto;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.recommend.entity.UserRecommends;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.public_api.dto.food.DailyFoodByDateDto;
import co.kurrant.app.public_api.dto.food.DailyFoodResDto;
import com.mysema.commons.lang.Pair;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", imports = {DateUtils.class, UserSupportPriceUtil.class})
public interface PublicDailyFoodMapper {
    default DailyFoodResDto toDailyFoodResDto(LocalDate startDate, LocalDate endDate, List<DailyFood> dailyFoods, Group group, Spot spot, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Map<DailyFood, Integer> dailyFoodCountMap, List<UserRecommends> userRecommendList, Map<BigInteger, Pair<Double, Long>> reviewMap, User user) {
        // 1. 해당 스팟의 정보 가져오기
        List<DailyFoodResDto.ServiceInfo> diningTypes = toDailyFoodResDtoServiceInfos(spot, group);

        // 2. 날짜별 지원금 및 식사 가져오기
        List<DailyFoodResDto.DailyFoodByDate> dailyFoodByDates = toDailyFoodResDtoDailyFoodByDate(startDate, endDate, dailyFoods, dailyFoodSupportPrices, dailyFoodCountMap, spot, userRecommendList, reviewMap, user);
        dailyFoodByDates = dailyFoodByDates.stream()
                .sorted(Comparator.comparing(DailyFoodResDto.DailyFoodByDate::getServiceDate).thenComparing(DailyFoodResDto.DailyFoodByDate::getDiningType))
                .toList();

        return new DailyFoodResDto(diningTypes, dailyFoodByDates);
    }

    default List<DailyFoodResDto.ServiceInfo> toDailyFoodResDtoServiceInfos(Spot spot, Group group) {
        List<DiningType> diningTypes = spot.getDiningTypes();
        List<DailyFoodResDto.ServiceInfo> diningTypeDtos = new ArrayList<>();
        for (DiningType diningType : diningTypes) {
            List<LocalTime> deliveryTimes = group.getMealInfo(diningType).getDeliveryTimes();
            List<String> deliveryTimesStr = deliveryTimes.stream()
                    .map(DateUtils::timeToString)
                    .toList();
            List<String> serviceDays = DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays());
            DailyFoodResDto.ServiceInfo diningTypeDto = new DailyFoodResDto.ServiceInfo(diningType.getCode(), serviceDays, deliveryTimesStr);

            // 요일별 식사 지원금
            if (Hibernate.getClass(group).equals(Corporation.class) && group.getMealInfo(diningType) != null) {
                List<DailyFoodResDto.SupportPriceByDay> supportPriceByDays = new ArrayList<>();
                CorporationMealInfo corporationMealInfo = (CorporationMealInfo) Hibernate.unproxy(group.getMealInfo(diningType));
                List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPrices = corporationMealInfo.getServiceDaysAndSupportPrices();
                for (ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPrices) {
                    for (Days supportDay : serviceDaysAndSupportPrice.getSupportDays()) {
                        supportPriceByDays.add(new DailyFoodResDto.SupportPriceByDay(supportDay.getDays(), serviceDaysAndSupportPrice.getSupportPrice()));
                    }
                }
                diningTypeDto.setSupportPriceByDays(supportPriceByDays);
            }
            diningTypeDtos.add(diningTypeDto);
        }
        return diningTypeDtos;
    }

    default List<DailyFoodResDto.DailyFoodByDate> toDailyFoodResDtoDailyFoodByDate(LocalDate startDate, LocalDate endDate, List<DailyFood> dailyFoods, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Map<DailyFood, Integer> dailyFoodCountMap, Spot spot, List<UserRecommends> userRecommendList, Map<BigInteger, Pair<Double, Long>> reviewMap, User user) {
        // 1. 그룹별 식사일정(DiningType)과 이용가능 요일 매핑을 가져온다.
        Map<DiningType, List<Days>> diningTypes = spot.getGroup().getDiningTypes().stream()
                .collect(Collectors.toMap(
                        v -> v,
                        v -> spot.getGroup().getMealInfo(v).getServiceDays()
                ));

        // 2. 각 식사일정별 이용 가능 날짜를 매핑한다.
        Map<DiningType, List<LocalDate>> allDates = new HashMap<>();
        for (Map.Entry<DiningType, List<Days>> entry : diningTypes.entrySet()) {
            DiningType diningType = entry.getKey();
            List<Days> daysList = entry.getValue();

            List<LocalDate> dateList = Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                    .filter(date -> daysList.contains(Days.toDaysEnum(date.getDayOfWeek())))
                    .collect(Collectors.toList());

            allDates.put(diningType, dateList);
        }
        // 3. 식단을 서비스 날짜와 식사일정별로 그룹핑한다.
        Map<AbstractMap.SimpleEntry<LocalDate, DiningType>, List<DailyFood>> dailyFoodMap = dailyFoods.stream()
                .collect(Collectors.groupingBy(df -> new AbstractMap.SimpleEntry<>(df.getServiceDate(), df.getDiningType())));

        // 4. 최종 Response 생성
        List<DailyFoodResDto.DailyFoodByDate> resultList = new ArrayList<>();
        for (Map.Entry<DiningType, List<LocalDate>> entry : allDates.entrySet()) {
            DiningType diningType = entry.getKey();

            for (LocalDate date : entry.getValue()) {
                List<DailyFood> foodsForDate = dailyFoodMap.getOrDefault(new AbstractMap.SimpleEntry<>(date, diningType), new ArrayList<>());

                List<DailyFoodDto> dailyFoodDtos = foodsForDate.stream().map(dailyFood -> {
                    Pair<Double, Long> reviewData = reviewMap.getOrDefault(dailyFood.getFood().getId(), new Pair<>(0.0, 0L));
                    Double reviewAverage = Math.round(reviewData.getFirst() * 100) / 100.0;
                    Integer totalCount = reviewData.getSecond().intValue();

                    Integer sort = sortByFoodTag(dailyFood);

                    DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                    return toDto(spot.getId(), dailyFood, discountDto, dailyFoodCountMap.get(dailyFood), userRecommendList, reviewAverage, totalCount, sort);

                }).sorted(Comparator.comparing(DailyFoodDto::getSort).reversed()).collect(Collectors.toList());

                DailyFoodResDto.DailyFoodByDate dailyFoodByDate = new DailyFoodResDto.DailyFoodByDate();
                dailyFoodByDate.setServiceDate(DateUtils.localDateToString(date));
                dailyFoodByDate.setDiningType(diningType.getCode());
                dailyFoodByDate.setSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, dailyFoodSupportPrices, date, diningType));
                dailyFoodByDate.setDailyFoodDtos(dailyFoodDtos);

                resultList.add(dailyFoodByDate);
            }
        }

        return resultList;

    }

    default DailyFoodByDateDto toDailyFoodByDateDto(LocalDate startDate, LocalDate endDate, List<DailyFood> dailyFoods, Group group, Spot spot, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Map<DailyFood, Integer> dailyFoodCountMap, List<UserRecommends> userRecommendList, Map<BigInteger, Pair<Double, Long>> reviewMap, User user) {
        // 1. 해당 스팟의 정보 가져오기
        List<DailyFoodByDateDto.ServiceInfo> diningTypes = toDailyFoodByDateDtoServiceInfos(spot, group);

        // 2. 날짜별 지원금 및 식사 가져오기
        List<DailyFoodByDateDto.DailyFoodGroupByDate> dailyFoodByDates = toDailyFoodGroupByDate(startDate, endDate, dailyFoods, dailyFoodSupportPrices, dailyFoodCountMap, spot, userRecommendList, reviewMap, user);
        dailyFoodByDates = dailyFoodByDates.stream()
                .sorted(Comparator.comparing(DailyFoodByDateDto.DailyFoodGroupByDate::getServiceDate))
                .toList();

        return new DailyFoodByDateDto(diningTypes, dailyFoodByDates);
    }

    default List<DailyFoodByDateDto.ServiceInfo> toDailyFoodByDateDtoServiceInfos(Spot spot, Group group) {
        List<DiningType> diningTypes = spot.getDiningTypes();
        List<DailyFoodByDateDto.ServiceInfo> diningTypeDtos = new ArrayList<>();
        for (DiningType diningType : diningTypes) {
            List<LocalTime> deliveryTimes = group.getMealInfo(diningType).getDeliveryTimes();
            List<String> deliveryTimesStr = deliveryTimes.stream()
                    .map(DateUtils::timeToString)
                    .toList();
            List<String> serviceDays = DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays());
            DailyFoodByDateDto.ServiceInfo diningTypeDto = new DailyFoodByDateDto.ServiceInfo(diningType.getCode(), serviceDays, deliveryTimesStr);

            // 요일별 식사 지원금
            if (group instanceof Corporation && group.getMealInfo(diningType) != null) {
                List<DailyFoodByDateDto.SupportPriceByDay> supportPriceByDays = new ArrayList<>();
                CorporationMealInfo corporationMealInfo = (CorporationMealInfo) Hibernate.unproxy(group.getMealInfo(diningType));
                List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPrices = corporationMealInfo.getServiceDaysAndSupportPrices();
                for (ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPrices) {
                    for (Days supportDay : serviceDaysAndSupportPrice.getSupportDays()) {
                        supportPriceByDays.add(new DailyFoodByDateDto.SupportPriceByDay(supportDay.getDays(), serviceDaysAndSupportPrice.getSupportPrice()));
                    }
                }
                diningTypeDto.setSupportPriceByDays(supportPriceByDays);
            }
            diningTypeDtos.add(diningTypeDto);
        }
        return diningTypeDtos;
    }

    default List<DailyFoodByDateDto.DailyFoodGroupByDate> toDailyFoodGroupByDate(LocalDate startDate, LocalDate endDate, List<DailyFood> dailyFoods, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Map<DailyFood, Integer> dailyFoodCountMap, Spot spot, List<UserRecommends> userRecommendList, Map<BigInteger, Pair<Double, Long>> reviewMap, User user) {
        // 1. 그룹별 식사일정(DiningType)과 이용가능 요일 매핑을 가져온다.
        Map<DiningType, List<Days>> diningTypes = spot.getGroup().getDiningTypes().stream()
                .collect(Collectors.toMap(
                        v -> v,
                        v -> spot.getGroup().getMealInfo(v).getServiceDays()
                ));

        // 2. 각 식사일정별 이용 가능 날짜를 매핑한다.
        Map<DiningType, List<LocalDate>> allDates = new HashMap<>();
        for (Map.Entry<DiningType, List<Days>> entry : diningTypes.entrySet()) {
            DiningType diningType = entry.getKey();
            List<Days> daysList = entry.getValue();

            List<LocalDate> dateList = Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                    .filter(date -> daysList.contains(Days.toDaysEnum(date.getDayOfWeek())))
                    .collect(Collectors.toList());

            allDates.put(diningType, dateList);
        }
        // 3. 식단을 서비스 날짜와 식사일정별로 그룹핑한다.
        Map<AbstractMap.SimpleEntry<LocalDate, DiningType>, List<DailyFood>> dailyFoodMap = dailyFoods.stream()
                .collect(Collectors.groupingBy(df -> new AbstractMap.SimpleEntry<>(df.getServiceDate(), df.getDiningType())));

        // 4. 최종 Response 생성
        List<DailyFoodByDateDto.DailyFoodGroupByDate> resultList = new ArrayList<>();
        Set<LocalDate> allUniqueDates = new HashSet<>();
        allDates.values().forEach(allUniqueDates::addAll);

        for (LocalDate date : allUniqueDates) {
            DailyFoodByDateDto.DailyFoodGroupByDate groupByDate = new DailyFoodByDateDto.DailyFoodGroupByDate();
            groupByDate.setServiceDate(DateUtils.localDateToString(date));

            List<DailyFoodByDateDto.DailyFoodByDate> dailyFoodByDates = new ArrayList<>();

            for (DiningType diningType : allDates.keySet()) {
                DailyFoodByDateDto.DailyFoodByDate dailyFoodByDate = new DailyFoodByDateDto.DailyFoodByDate();
                BigDecimal supportPrice = UserSupportPriceUtil.getUsableSupportPrice(spot, dailyFoodSupportPrices, date, diningType);
                SupportType supportType = UserSupportPriceUtil.getSupportType(supportPrice);
                dailyFoodByDate.setDiningType(diningType.getCode());
                dailyFoodByDate.setSupportPrice(supportType.equals(SupportType.FIXED) ? supportPrice : null);
                dailyFoodByDate.setSupportPercent(supportType.equals(SupportType.PARTIAL) ? supportPrice : null);

                List<DailyFood> foodsForDate = dailyFoodMap.getOrDefault(new AbstractMap.SimpleEntry<>(date, diningType), new ArrayList<>());

                List<DailyFoodDto> dailyFoodDtos = foodsForDate.stream().map(dailyFood -> {
                    Pair<Double, Long> reviewData = reviewMap.getOrDefault(dailyFood.getFood().getId(), new Pair<>(0.0, 0L));
                    Double reviewAverage = Math.round(reviewData.getFirst() * 100) / 100.0;
                    Integer totalCount = reviewData.getSecond().intValue();

                    Integer sort = sortByFoodTag(dailyFood);
                    DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                    return toDto(spot.getId(), dailyFood, discountDto, dailyFoodCountMap.get(dailyFood), userRecommendList, reviewAverage, totalCount, sort);

                }).sorted(Comparator.comparing(DailyFoodDto::getSort).reversed()).collect(Collectors.toList());

                dailyFoodByDate.setDailyFoodDtos(dailyFoodDtos);
                dailyFoodByDates.add(dailyFoodByDate);
            }

            dailyFoodByDates = dailyFoodByDates.stream().sorted(Comparator.comparing(DailyFoodByDateDto.DailyFoodByDate::getDiningType)).toList();

            groupByDate.setDailyFoodDtos(dailyFoodByDates);
            resultList.add(groupByDate);
        }

        return resultList;
    }


    @Mapping(source = "sort", target = "sort")
    @Mapping(source = "dailyFood", target = "lastOrderTime", qualifiedByName = "getLastOrderTime")
    @Mapping(source = "totalCount", target = "totalReviewCount")
    @Mapping(source = "reviewAverage", target = "reviewAverage")
    @Mapping(source = "dailyFood.diningType.code", target = "diningType")
    @Mapping(source = "dailyFood.food.id", target = "foodId")
    @Mapping(source = "dailyFood.food.name", target = "foodName")
    @Mapping(source = "dailyFood.dailyFoodStatus", target = "status", qualifiedByName = "getStatus")
    @Mapping(source = "spotId", target = "spotId")
    @Mapping(source = "dailyFood.serviceDate", target = "serviceDate", qualifiedByName = "serviceDateToString")
    @Mapping(source = "dailyFood", target = "makersName", qualifiedByName = "getMakersName")
    @Mapping(source = "dailyFood", target = "spicy", qualifiedByName = "getSpicy")
    @Mapping(source = "dailyFood", target = "vegan", qualifiedByName = "getVegan")
    @Mapping(target = "image", expression = "java(dailyFood.getFood().getImages() == null || dailyFood.getFood().getImages().isEmpty() ? null : dailyFood.getFood().getImages().get(0).getLocation())")
    @Mapping(source = "dailyFood.food.description", target = "description")
    @Mapping(source = "dailyFood.food.price", target = "price")
    @Mapping(target = "discountedPrice", expression = "java(discountDto.getDiscountedPrice())")
    @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountPrice")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
    @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
    DailyFoodDto toDto(BigInteger spotId, DailyFood dailyFood, DiscountDto discountDto, Integer capacity, List<UserRecommends> userRecommends, double reviewAverage, Integer totalCount, Integer sort);

    @Named("getLastOrderTime")
    default String getLastOrderTime(DailyFood dailyFood) {
        DayAndTime makersLastOrderTime = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime mealInfoLastOrderTime = dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime foodLastOrderTime = dailyFood.getFood().getFoodCapacity(dailyFood.getDiningType()).getLastOrderTime();

        List<DayAndTime> lastOrderTimes = Stream.of(makersLastOrderTime, mealInfoLastOrderTime, foodLastOrderTime)
                .filter(Objects::nonNull) // Exclude null values
                .toList();
        DayAndTime lastOrderTime = lastOrderTimes.stream().min(Comparator.comparing(DayAndTime::getDay).reversed().thenComparing(DayAndTime::getTime))
                .orElse(null);

        return DayAndTime.dayAndTimeToString(lastOrderTime);
    }

    @Named("getStatus")
    default Integer getStatus(DailyFoodStatus dailyFoodStatus) {
        return dailyFoodStatus.getCode();
    }

    @Named("serviceDateToString")
    default String serviceDateToString(LocalDate serviceDate) {
        return DateUtils.format(serviceDate, "yyyy-MM-dd");
    }

    @Named("getMembershipDiscountedRate")
    default Integer getMembershipDiscountedRate(List<FoodDiscountPolicy> foodDiscountPolicyList) {
        Optional<FoodDiscountPolicy> foodDiscountPolicyOptional = foodDiscountPolicyList.stream()
                .filter(v -> v.getDiscountType().equals(DiscountType.MEMBERSHIP_DISCOUNT))
                .findAny();
        return foodDiscountPolicyOptional.map(FoodDiscountPolicy::getDiscountRate).orElse(null);
    }

    @Named("getSpicy")
    default String getSpicy(DailyFood dailyFood) {
        List<FoodTag> foodTags = dailyFood.getFood().getFoodTags();
        Optional<FoodTag> foodTag = foodTags.stream().filter(v -> v.getCategory().equals("맵기") && !v.getCode().equals(10001)).findAny();
        return foodTag.map(FoodTag::getTag).orElse(null);
    }

    @Named("getVegan")
    default String getVegan(DailyFood dailyFood) {
        List<FoodTag> foodTags = dailyFood.getFood().getFoodTags();
        Optional<FoodTag> foodTag = foodTags.stream().filter(v -> v.getCode().equals(9001)).findAny();
        return (foodTag.isPresent()) ? "Vegan" : null;
    }

    @Named("getMakersName")
    default String getMakersName(DailyFood dailyFood) {
        Makers makers = (Makers) Hibernate.unproxy(dailyFood.getFood().getMakers());
        return makers.getName();
    }

    default Integer sortByFoodTag(DailyFood dailyFood) {
        if (!dailyFood.getFood().getFoodTags().isEmpty()) {
            //판매중이 아닌 상품은 10부터 시작
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11003))) {    //정찬도시락
                return 10;
            }
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11007))) {    //한그릇음식
                return 9;
            }
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11004))) {    //산후조리식
                return 8;
            }
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11005))) {    //다이어트식
                return 7;
            }
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11006))) {    //프로틴식
                return 6;
            }
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11002))) {    //샐러드
                return 5;
            }
            if (dailyFood.getDailyFoodStatus().getCode() != 1 && dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11001))) {    //간편식
                return 4;
            }
            //판매중인 상품은 20부터 시작
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11003))) {    //정찬도시락
                return 20;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11007))) {    //한그릇음식
                return 19;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11004))) {    //산후조리식
                return 18;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11005))) {    //다이어트식
                return 17;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11006))) {    //프로틴식
                return 16;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11002))) {    //샐러드
                return 15;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11001))) {    //간편식
                return 14;
            }
        }
        return 0;
    }

}
