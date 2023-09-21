package co.dalicious.domain.food.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.embebbed.DeliverySchedule;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.recommend.entity.UserRecommends;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.dto.DailyFoodDto;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface DailyFoodMapper {
    default DailyFood toDailyFood(PresetDailyFood presetDailyFood, DailyFoodGroup dailyFoodGroup) {
        return DailyFood.builder()
                .diningType(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getDiningType())
                .dailyFoodStatus(DailyFoodStatus.WAITING_SALE)
                .serviceDate(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getServiceDate())
                .supplyPrice(presetDailyFood.getFood().getSupplyPrice())
                .defaultPrice(presetDailyFood.getFood().getPrice())
                .membershipDiscountRate(presetDailyFood.getFood().getFoodDiscountRate(DiscountType.MEMBERSHIP_DISCOUNT))
                .makersDiscountRate(presetDailyFood.getFood().getFoodDiscountRate(DiscountType.MAKERS_DISCOUNT))
                .periodDiscountRate(presetDailyFood.getFood().getFoodDiscountRate(DiscountType.PERIOD_DISCOUNT))
                .food(presetDailyFood.getFood())
                .group(presetDailyFood.getPresetGroupDailyFood().getGroup())
                .dailyFoodGroup(dailyFoodGroup)
                .build();
    }

    default DailyFood toEatInDailyFood(Food food, LocalDate serviceDate, DiningType diningType, Group group, DailyFoodGroup dailyFoodGroup) {
        return DailyFood.builder()
                .diningType(diningType)
                .dailyFoodStatus(DailyFoodStatus.SALES)
                .serviceDate(serviceDate)
                .supplyPrice(food.getSupplyPrice())
                .defaultPrice(food.getPrice())
                .membershipDiscountRate(food.getFoodDiscountRate(DiscountType.MEMBERSHIP_DISCOUNT))
                .makersDiscountRate(food.getFoodDiscountRate(DiscountType.MAKERS_DISCOUNT))
                .periodDiscountRate(food.getFoodDiscountRate(DiscountType.PERIOD_DISCOUNT))
                .isEatIn(true)
                .food(food)
                .group(group)
                .dailyFoodGroup(dailyFoodGroup)
                .build();
    }

    @Mapping(source = "presetGroupDailyFood.deliveryScheduleList", target = "deliverySchedules")
    DailyFoodGroup toDailyFoodGroup(PresetGroupDailyFood presetGroupDailyFood);

    default DailyFoodGroup toDailyFoodGroup(Map<String, String> deliveryScheduleMap) {
        List<DeliverySchedule> deliveryScheduleList = new ArrayList<>();

        deliveryScheduleMap.keySet().forEach(deliveryTime -> {
            DeliverySchedule deliverySchedule = new DeliverySchedule(DateUtils.stringToLocalTime(deliveryTime), DateUtils.stringToLocalTime(deliveryScheduleMap.get(deliveryTime)));
            deliveryScheduleList.add(deliverySchedule);
        });

        return new DailyFoodGroup(deliveryScheduleList);
    }

    default DailyFoodGroup toDailyFoodGroup(List<LocalTime> deliveryTimes) {
        List<DeliverySchedule> deliveryScheduleList = new ArrayList<>();

        deliveryTimes.forEach(deliveryTime -> {
            DeliverySchedule deliverySchedule = new DeliverySchedule(deliveryTime, deliveryTime.minusMinutes(45));
            deliveryScheduleList.add(deliverySchedule);
        });

        return new DailyFoodGroup(deliveryScheduleList);
    }

    default List<DailyFood> toDailyFoods(MultiValueMap<DailyFoodGroup, FoodDto.DailyFood> dailyFoodMap, List<Group> groups, List<Food> foods) {
        List<DailyFood> dailyFoods = new ArrayList<>();
        for (DailyFoodGroup dailyFoodGroup : dailyFoodMap.keySet()) {
            List<FoodDto.DailyFood> dailyFoodDtos = dailyFoodMap.get(dailyFoodGroup);
            for (FoodDto.DailyFood dailyFoodDto : dailyFoodDtos) {
                DailyFood dailyFood = toDailyFood(groups, dailyFoodDto, foods, dailyFoodGroup);
                // 메이커스/음식 주문 가능 수량이 존재하지 않을 경우
                if(dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()) == null) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000018", dailyFood.getFood().getMakers().getId() + "번 메이커스의 " + dailyFood.getDiningType().getDiningType() + " 주문 가능 수량이 존재하지 않습니다.");
                }
                if(dailyFood.getFood().getFoodCapacity(dailyFood.getDiningType()) == null) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000019", dailyFood.getFood().getId() + "번 음식의 " + dailyFood.getDiningType().getDiningType() + " 주문 가능 수량이 존재하지 않습니다.");
                }
                dailyFoods.add(dailyFood);
            }
        }
        return dailyFoods;
    }


    default DailyFood toDailyFood(List<Group> groups, FoodDto.DailyFood dailyFoodDto, List<Food> foods, DailyFoodGroup dailyFoodGroup) {
        Food food = Food.getFood(foods, dailyFoodDto.getMakersName(), dailyFoodDto.getFoodName());
        if (food == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "CE400020", "'" + dailyFoodDto.getMakersName() + "'의 '" + dailyFoodDto.getFoodName() + "' 상품 정보를 찾을 수 없습니다.");
        }
        Group group = Group.getGroup(groups, dailyFoodDto.getGroupName());
        if (group == null) throw new ApiException(ExceptionEnum.GROUP_NOT_FOUND);
        // 그룹이 가지고 있지 않은 식사 일정을 추가할 경우
        if (!group.getDiningTypes().contains(DiningType.ofCode(dailyFoodDto.getDiningType()))) {
            throw new ApiException(ExceptionEnum.GROUP_DOSE_NOT_HAVE_DINING_TYPE);
        }
        return DailyFood.builder()
                .dailyFoodGroup(dailyFoodGroup)
                .dailyFoodStatus(DailyFoodStatus.ofCode(dailyFoodDto.getFoodStatus()))
                .diningType(DiningType.ofCode(dailyFoodDto.getDiningType()))
                .serviceDate(DateUtils.stringToDate(dailyFoodDto.getServiceDate()))
                .supplyPrice(food.getSupplyPrice())
                .defaultPrice(food.getPrice())
                .membershipDiscountRate(food.getFoodDiscountRate(DiscountType.MEMBERSHIP_DISCOUNT))
                .makersDiscountRate(food.getFoodDiscountRate(DiscountType.MAKERS_DISCOUNT))
                .periodDiscountRate(food.getFoodDiscountRate(DiscountType.PERIOD_DISCOUNT))
                .food(food)
                .group(group)
                .build();
    }


    @Mapping(source = "sort", target = "sort")
    @Mapping(source = "lastOrderTime", target = "lastOrderTime")
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
    DailyFoodDto toDto(BigInteger spotId, DailyFood dailyFood, DiscountDto discountDto, Integer capacity, List<UserRecommends> userRecommends, double reviewAverage, Integer totalCount, Integer sort, String lastOrderTime);

    @AfterMapping
    default void afterMapping(@MappingTarget DailyFoodDto dto, DailyFood dailyFood, Integer capacity, List<UserRecommends> userRecommends) {
        MakersCapacity makersCapacity = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType());
        dto.setMinTime(makersCapacity == null ? null : DateUtils.timeToString(makersCapacity.getMinTime()));
        dto.setMaxTime(makersCapacity == null ? null : DateUtils.timeToString(makersCapacity.getMaxTime()));
        dto.setCapacity(capacity);
        // 랭크 추가
        userRecommends.stream().filter(recommend ->
                        recommend.getFoodId().equals(dto.getFoodId()) && recommend.getDiningType().getCode().equals(dto.getDiningType())).findFirst()
                .ifPresent(userRecommend -> dto.setRank(userRecommend.getRank()));
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

    default void updateDeliverySchedule(List<String> deliveryTimeList, List<String> pickupTimeList, @MappingTarget DailyFoodGroup dailyFoodGroup) {
        if(deliveryTimeList.size() != pickupTimeList.size()) {
            throw new ApiException(ExceptionEnum.EXCEL_TIME_LIST_NOT_EQUAL);
        }
        List<DeliverySchedule> newDeliveryScheduleList = new ArrayList<>();
        for (String deliveryTimeString : deliveryTimeList) {
            LocalTime deliveryTime = DateUtils.stringToLocalTime(deliveryTimeString);
            LocalTime pickupTime = DateUtils.stringToLocalTime(pickupTimeList.get(deliveryTimeList.indexOf(deliveryTimeString)));
            newDeliveryScheduleList.add(new DeliverySchedule(deliveryTime, pickupTime));
        }

        dailyFoodGroup.updateDeliverySchedules(newDeliveryScheduleList);
    }

    default void updateDailyFoodStatus(DailyFoodStatus status, @MappingTarget DailyFood dailyFood) {
        dailyFood.updateDailyFoodStatus(status);
    }
}