package co.kurrant.app.public_api.mapper.food;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
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
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.public_api.dto.food.DailyFoodResDto;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, UserSupportPriceUtil.class})
public interface PublicDailyFoodMapper {
    default DailyFoodResDto toDailyFoodResDto(List<DailyFood> dailyFoods, Group group, Spot spot, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Map<DailyFood, Integer> dailyFoodCountMap, List<UserRecommends> userRecommendList, List<Reviews> reviewList, User user) {
        // 1. 해당 스팟의 정보 가져오기
        List<DailyFoodResDto.ServiceInfo> diningTypes = toDailyFoodResDtoServiceInfos(spot, group);

        // 2. 날짜별 지원금 및 식사 가져오기
        List<DailyFoodResDto.DailyFoodByDate> dailyFoodByDates = toDailyFoodResDtoDailyFoodByDate(dailyFoods, dailyFoodSupportPrices, dailyFoodCountMap, spot, userRecommendList, reviewList, user);

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
            if(Hibernate.getClass(group).equals(Corporation.class) && group.getMealInfo(diningType) != null) {
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

    default List<DailyFoodResDto.DailyFoodByDate> toDailyFoodResDtoDailyFoodByDate(List<DailyFood> dailyFoods, List<DailyFoodSupportPrice> dailyFoodSupportPrices, Map<DailyFood, Integer> dailyFoodCountMap, Spot spot, List<UserRecommends> userRecommendList, List<Reviews> reviewList, User user) {
        return dailyFoods.stream()
                .collect(Collectors.groupingBy(v -> new AbstractMap.SimpleEntry<>(v.getServiceDate(), v.getDiningType())))
                .entrySet().stream()
                .map(entry -> {
                    List<DailyFoodDto> dailyFoodDtos = new ArrayList<>();
                    for (DailyFood dailyFood : entry.getValue()) {
                        int sumStar = 0;

                        List<Reviews> totalReviewsList = reviewList.stream()
                                .filter(v -> v.getFood().equals(dailyFood.getFood()))
                                .toList();
                        for (Reviews reviews : totalReviewsList) {
                            sumStar += reviews.getSatisfaction();
                        }

                        Integer totalCount = totalReviewsList.size();
                        Double reviewAverage = Math.round(sumStar / (double) totalCount * 100) / 100.0;

                        Integer sort = sortByFoodTag(dailyFood);

                        DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                        DailyFoodDto dailyFoodDto = toDto(spot.getId(), dailyFood, discountDto, dailyFoodCountMap.get(dailyFood), userRecommendList, reviewAverage, totalCount, sort);
                        dailyFoodDtos.add(dailyFoodDto);
                    }
                    DailyFoodResDto.DailyFoodByDate dailyFoodByDate = new DailyFoodResDto.DailyFoodByDate();
                    dailyFoodByDate.setServiceDate(DateUtils.localDateToString(entry.getKey().getKey()));
                    dailyFoodByDate.setDiningType(entry.getKey().getValue().getCode());
                    dailyFoodByDate.setSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, dailyFoodSupportPrices, entry.getKey().getKey(), entry.getKey().getValue()));
                    dailyFoodByDate.setDailyFoodDtos(dailyFoodDtos.stream().sorted(Comparator.comparing(DailyFoodDto::getSort).reversed()).toList());
                    return dailyFoodByDate;
                })
                .toList();

    }

    @Mapping(source = "sort", target = "sort")
    @Mapping(source = "dailyFood", target = "lastOrderTime" , qualifiedByName = "getLastOrderTime")
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
    default String getLastOrderTime(DailyFood dailyFood){
        DayAndTime makersLastOrderTime = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime mealInfoLastOrderTime = dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime();
        DayAndTime foodLastOrderTime = dailyFood.getFood().getFoodCapacity(dailyFood.getDiningType()).getLastOrderTime();

        //메이커스의 주문 마감시간이 null이 아니고, 밀인포 마감시간과 상품 마감시간 보다 빠를때는 메이커스 마감시간을 리턴한다.
        if (makersLastOrderTime != null && DayAndTime.isBefore(makersLastOrderTime, mealInfoLastOrderTime)){
            if (foodLastOrderTime != null && DayAndTime.isBefore(makersLastOrderTime, foodLastOrderTime)){
                return makersLastOrderTime.dayAndTimeToStringByDate(dailyFood.getServiceDate());
            } else if (foodLastOrderTime != null && DayAndTime.isBefore(foodLastOrderTime, makersLastOrderTime)){
                return foodLastOrderTime.dayAndTimeToStringByDate(dailyFood.getServiceDate());
            }
        }

        //위에 조건에 해당되지 않고 상품 마감시간이 밀인포 마감시간보다 빠르면 상품 마감시간 리턴
        if (foodLastOrderTime != null && DayAndTime.isBefore(foodLastOrderTime, mealInfoLastOrderTime)){
            return foodLastOrderTime.dayAndTimeToStringByDate(dailyFood.getServiceDate());
        }

        return mealInfoLastOrderTime.dayAndTimeToStringByDate(dailyFood.getServiceDate());

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
