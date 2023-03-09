package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.entity.UserSupportPriceHistory;
import co.dalicious.domain.order.repository.UserSupportPriceHistoryRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.recommend.dto.UserRecommendWhereData;
import co.dalicious.domain.recommend.entity.UserRecommends;
import co.dalicious.domain.recommend.repository.QUserRecommendRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.order.mapper.FoodMapper;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final UserUtil userUtil;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final DailyFoodMapper dailyFoodMapper;
    private final FoodMapper foodMapper;
    private final SpotRepository spotRepository;
    private final DailyFoodRepository dailyFoodRepository;
    private final OrderDailyFoodUtil orderDailyFoodUtil;
    private final QUserRecommendRepository qUserRecommendRepository;
    private final UserSupportPriceHistoryRepository userSupportPriceHistoryRepository;


    @Override
    @Transactional
    public RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate, Integer diningTypeCode) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        // 스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 그룹 정보 가져오기
        Group group = spot.getGroup();
        // 유저가 그 그룹의 스팟에 포함되는지 확인.
        List<UserGroup> userGroups = user.getGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group) && v.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        List<DailyFood> dailyFoodList;
        List<DailyFoodDto> dailyFoodDtos = new ArrayList<>();

        // TODO: Spring Batch 서버 구현 완료시 스케쥴러로 변경하기
        List<MealInfo> mealInfoList = spot.getMealInfos();

        if(diningTypeCode != null) {
            DiningType diningType = DiningType.ofCode(diningTypeCode);
            dailyFoodList = qDailyFoodRepository.findAllByGroupAndSelectedDateAndDiningType(group, selectedDate, diningType);

            for (DailyFood dailyFood : dailyFoodList) {
                // TODO: Spring Batch 서버 구현 완료시 스케쥴러로 변경하기
                MealInfo mealInfo = spot.getMealInfo(dailyFood.getDiningType());
                LocalDateTime lastOrderDateTime = LocalDateTime.of(dailyFood.getServiceDate().minusDays(mealInfo.getLastOrderTime().getDay()), mealInfo.getLastOrderTime().getTime());
                if(LocalDate.now().equals(dailyFood.getServiceDate()) && LocalDateTime.now().isAfter(lastOrderDateTime)) {
                    dailyFood.updateFoodStatus(DailyFoodStatus.PASS_LAST_ORDER_TIME);
                }

                DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(spotId, dailyFood, discountDto);
                dailyFoodDto.setCapacity(orderDailyFoodUtil.getRemainFoodCount(dailyFood).getRemainCount());
                dailyFoodDtos.add(dailyFoodDto);
            }

            // recommend 가져오기
            List<BigInteger> foodIds = dailyFoodDtos.stream().map(DailyFoodDto::getFoodId).collect(Collectors.toList());
            List<UserRecommends> userRecommendList = qUserRecommendRepository.getUserRecommends(
                    UserRecommendWhereData.createUserRecommendWhereData(user.getId(), group.getId(), foodIds, selectedDate));
            if(!userRecommendList.isEmpty() && user.getIsMembership()){
                // dto에 랭크 추가
                dailyFoodDtos.forEach(dto -> {
                    userRecommendList.stream().filter(recommend ->
                                    recommend.getFoodId().equals(dto.getFoodId()) && recommend.getDiningType().getCode().equals(dto.getDiningType())).findFirst()
                            .ifPresent(userRecommend -> dto.setRank(userRecommend.getRank()));
                });
                dailyFoodDtos = dailyFoodDtos.stream().sorted((dto1, dto2) -> {
                            if (dto1.getRank() == null) {
                                return 1;
                            }
                            if (dto2.getRank() == null) {
                                return -1;
                            }
                            return dto1.getRank().compareTo(dto2.getRank());
                        }).toList();
            }



            return RetrieveDailyFoodDto.builder()
                    .dailyFoodDtos(dailyFoodDtos)
                    .build();
        }
        else {
            // 유저가 당일날에 해당하는 식사타입이 몇 개인지 확인
            List<Integer> diningTypes = new ArrayList<>();
            for (DiningType diningType : spot.getDiningTypes()) {
                diningTypes.add(diningType.getCode());
            }
            // 결과값을 담아줄 LIST 생성
            // 조건에 맞는 DailyFood 조회
            dailyFoodList = qDailyFoodRepository.getSellingAndSoldOutDailyFood(group, selectedDate);
            // 값이 있다면 결과값으로 담아준다.
            for (DailyFood dailyFood : dailyFoodList) {
                // TODO: Spring Batch 서버 구현 완료시 스케쥴러로 변경하기
                MealInfo mealInfo = spot.getMealInfo(dailyFood.getDiningType());
                LocalDateTime lastOrderDateTime = LocalDateTime.of(dailyFood.getServiceDate().minusDays(mealInfo.getLastOrderTime().getDay()), mealInfo.getLastOrderTime().getTime());
                if(LocalDate.now().equals(dailyFood.getServiceDate()) || LocalDate.now().isAfter(dailyFood.getServiceDate()) && LocalDateTime.now().isAfter(lastOrderDateTime)) {
                    dailyFood.updateFoodStatus(DailyFoodStatus.PASS_LAST_ORDER_TIME);
                }

                DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(spotId, dailyFood, discountDto);
                dailyFoodDto.setCapacity(orderDailyFoodUtil.getRemainFoodCount(dailyFood).getRemainCount());
                dailyFoodDtos.add(dailyFoodDto);
            }

            // recommend 가져오기
            List<BigInteger> foodIds = dailyFoodDtos.stream().map(DailyFoodDto::getFoodId).collect(Collectors.toList());
            List<UserRecommends> userRecommendList = qUserRecommendRepository.getUserRecommends(
                    UserRecommendWhereData.createUserRecommendWhereData(user.getId(), group.getId(), foodIds, selectedDate));

            if(!userRecommendList.isEmpty() && user.getIsMembership()){
                // dto에 랭크 추가
                dailyFoodDtos.forEach(dto -> {
                    userRecommendList.stream().filter(recommend ->
                                    recommend.getFoodId().equals(dto.getFoodId()) && recommend.getDiningType().getCode().equals(dto.getDiningType())).findFirst()
                            .ifPresent(userRecommend -> dto.setRank(userRecommend.getRank()));
                });

                dailyFoodDtos = dailyFoodDtos.stream().sorted((dto1, dto2) -> {
                    if (dto1.getRank() == null) {
                        return 1;
                    }
                    if (dto2.getRank() == null) {
                        return -1;
                    }
                    return dto1.getRank().compareTo(dto2.getRank());
                }).toList();
            }
            // 대상이 기업이라면 일일 지원금 필요
            RetrieveDailyFoodDto.SupportPrice supportPriceDto = new RetrieveDailyFoodDto.SupportPrice();
            if( Hibernate.unproxy(group) instanceof Corporation) {
                List<UserSupportPriceHistory> userSupportPriceHistories = userSupportPriceHistoryRepository.findAllByUserAndGroupAndServiceDate(user, group, selectedDate);
                for (Integer diningType : diningTypes) {
                    switch (DiningType.ofCode(diningType)) {
                        case MORNING -> supportPriceDto.setMorningSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, selectedDate, DiningType.MORNING));
                        case LUNCH -> supportPriceDto.setLunchSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, selectedDate, DiningType.LUNCH));
                        case DINNER -> supportPriceDto.setDinnerSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, selectedDate, DiningType.DINNER));
                    }
                }
            }

            return RetrieveDailyFoodDto.builder()
                    .diningTypes(diningTypes)
                    .supportPrice(supportPriceDto)
                    .dailyFoodDtos(dailyFoodDtos)
                    .build();
        }
    }

    @Override
    @Transactional
    public FoodDetailDto getFoodDetail(BigInteger dailyFoodId, SecurityUser securityUser) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );

        Spot spot = user.getDefaultUserSpot().getSpot();
        DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, dailyFood.getGroup(), spot, dailyFood);
        FoodDetailDto foodDetailDto = foodMapper.toDto(dailyFood, discountDto);
        foodDetailDto.setCapacity(orderDailyFoodUtil.getRemainFoodCount(dailyFood).getRemainCount());
        return foodDetailDto;
    }

    @Override
    @Transactional
    public RetrieveDiscountDto getFoodDiscount(BigInteger dailyFoodId) {
        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );

        Food food = dailyFood.getFood();
        DiscountDto discountDto = DiscountDto.getDiscount(food);
        return new RetrieveDiscountDto(discountDto);
    }
}