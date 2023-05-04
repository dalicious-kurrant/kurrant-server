package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.repository.DailyFoodSupportPriceRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.recommend.dto.UserRecommendWhereData;
import co.dalicious.domain.recommend.entity.UserRecommends;
import co.dalicious.domain.recommend.repository.QUserRecommendRepository;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import co.dalicious.domain.review.dto.ReviewListDto;
import co.dalicious.domain.review.dto.ReviewsForUserResDto;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.CommentsRepository;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.review.repository.ReviewRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.order.mapper.FoodMapper;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.model.SecurityUser;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final DailyFoodSupportPriceRepository dailyFoodSupportPriceRepository;
    private final ReviewRepository reviewRepository;
    private final QReviewRepository qReviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    private final CommentsRepository commentsRepository;

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

        if (diningTypeCode != null) {
            DiningType diningType = DiningType.ofCode(diningTypeCode);
            dailyFoodList = qDailyFoodRepository.findAllByGroupAndSelectedDateAndDiningType(group, selectedDate, diningType);

            for (DailyFood dailyFood : dailyFoodList) {
                DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(spotId, dailyFood, discountDto);
                dailyFoodDto.setCapacity(orderDailyFoodUtil.getRemainFoodCount(dailyFood).getRemainCount());
                dailyFoodDtos.add(dailyFoodDto);
            }

            // recommend 가져오기
            List<BigInteger> foodIds = dailyFoodDtos.stream().map(DailyFoodDto::getFoodId).collect(Collectors.toList());
            List<UserRecommends> userRecommendList = qUserRecommendRepository.getUserRecommends(
                    UserRecommendWhereData.createUserRecommendWhereData(user.getId(), group.getId(), foodIds, selectedDate));
            if (!userRecommendList.isEmpty() && user.getIsMembership()) {
                // dto에 랭크 추가
                dailyFoodDtos.forEach(dto -> {
                    userRecommendList.stream().filter(recommend ->
                                    recommend.getFoodId().equals(dto.getFoodId()) && recommend.getDiningType().getCode().equals(dto.getDiningType())).findFirst()
                            .ifPresent(userRecommend -> dto.setRank(userRecommend.getRank()));
                });
                dailyFoodDtos = dailyFoodDtos.stream()
                        .sorted(Comparator.<DailyFoodDto>comparingInt(dto -> dto.getRank() != null && dto.getRank().equals(1) ? 0 : 1)
                                .thenComparing(DailyFoodDto::getStatus)).toList();
            }


            return RetrieveDailyFoodDto.builder()
                    .dailyFoodDtos(dailyFoodDtos)
                    .build();
        } else {
            // 유저가 당일날에 해당하는 식사타입이 몇 개인지 확인
            List<Integer> diningTypes = new ArrayList<>();
            RetrieveDailyFoodDto.ServiceDays serviceDays = new RetrieveDailyFoodDto.ServiceDays();
            for (DiningType diningType : spot.getDiningTypes()) {
                diningTypes.add(diningType.getCode());

                // 이용가능 날짜
                switch (diningType) {
                    case MORNING ->
                            serviceDays.setMorningServiceDays(DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays()));
                    case LUNCH ->
                            serviceDays.setLunchServiceDays(DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays()));
                    case DINNER ->
                            serviceDays.setDinnerServiceDays(DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays()));
                }
            }
            // 결과값을 담아줄 LIST 생성
            // 조건에 맞는 DailyFood 조회
            dailyFoodList = qDailyFoodRepository.getSellingAndSoldOutDailyFood(group, selectedDate);
            // 값이 있다면 결과값으로 담아준다.
            for (DailyFood dailyFood : dailyFoodList) {
                /* FIXME: Spring Batch 서버 구현 완료
                MealInfo mealInfo = group.getMealInfo(dailyFood.getDiningType());
                LocalDateTime lastOrderDateTime = LocalDateTime.of(dailyFood.getServiceDate().minusDays(mealInfo.getLastOrderTime().getDay()), mealInfo.getLastOrderTime().getTime());
                if ((LocalDate.now().equals(dailyFood.getServiceDate()) || LocalDate.now().isAfter(dailyFood.getServiceDate())) && LocalDateTime.now().isAfter(lastOrderDateTime)) {
                    dailyFood.updateFoodStatus(DailyFoodStatus.SOLD_OUT);
                }
                 */
                DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, spot.getGroup(), spot, dailyFood);
                DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(spotId, dailyFood, discountDto);
                dailyFoodDto.setCapacity(orderDailyFoodUtil.getRemainFoodCount(dailyFood).getRemainCount());
                dailyFoodDtos.add(dailyFoodDto);
            }

            // recommend 가져오기
            List<BigInteger> foodIds = dailyFoodDtos.stream().map(DailyFoodDto::getFoodId).collect(Collectors.toList());
            List<UserRecommends> userRecommendList = qUserRecommendRepository.getUserRecommends(
                    UserRecommendWhereData.createUserRecommendWhereData(user.getId(), group.getId(), foodIds, selectedDate));

            if (!userRecommendList.isEmpty() && user.getIsMembership()) {
                // dto에 랭크 추가
                dailyFoodDtos.forEach(dto -> {
                    userRecommendList.stream().filter(recommend ->
                                    recommend.getFoodId().equals(dto.getFoodId()) && recommend.getDiningType().getCode().equals(dto.getDiningType())).findFirst()
                            .ifPresent(userRecommend -> dto.setRank(userRecommend.getRank()));
                });

                dailyFoodDtos = dailyFoodDtos.stream()
                        .sorted(Comparator.<DailyFoodDto>comparingInt(dto -> dto.getRank() != null && dto.getRank().equals(1) ? 0 : 1)
                                .thenComparing(DailyFoodDto::getStatus)).toList();
            }
            // 대상이 기업이라면 일일 지원금 필요
            RetrieveDailyFoodDto.SupportPrice supportPriceDto = new RetrieveDailyFoodDto.SupportPrice();
            if (Hibernate.unproxy(group) instanceof Corporation) {
                List<DailyFoodSupportPrice> userSupportPriceHistories = dailyFoodSupportPriceRepository.findAllByUserAndGroupAndServiceDate(user, group, selectedDate);
                for (Integer diningType : diningTypes) {
                    switch (DiningType.ofCode(diningType)) {
                        case MORNING ->
                                supportPriceDto.setMorningSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, selectedDate, DiningType.MORNING));
                        case LUNCH ->
                                supportPriceDto.setLunchSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, selectedDate, DiningType.LUNCH));
                        case DINNER ->
                                supportPriceDto.setDinnerSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, userSupportPriceHistories, selectedDate, DiningType.DINNER));
                    }
                }
            }

            return RetrieveDailyFoodDto.builder()
                    .diningTypes(diningTypes)
                    .supportPrice(supportPriceDto)
                    .dailyFoodDtos(dailyFoodDtos)
                    .serviceDays(serviceDays)
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
        // TODO: 식단에 가격 업데이트 적용이 되는 시점부터 주석 해제
//        DiscountDto discountDto = DiscountDto.getDiscount(dailyFood);
        DiscountDto discountDto = DiscountDto.getDiscount(dailyFood.getFood());
        return new RetrieveDiscountDto(discountDto);
    }

    @Override
    @Transactional
    public Object getFoodReview(BigInteger dailyFoodId, SecurityUser securityUser, Integer sort, Integer photo, Integer starFilter) {

        //유저와 DailyFood 정보 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        //리뷰와 유저정보 가져오기
        List<Reviews> reviewsList = new ArrayList<>();
        if (sort != null && sort != 0 ){
             reviewsList = qReviewRepository.findAllByfoodIdSort(dailyFood.getFood().getId(), sort, photo, starFilter);
        } else {
            reviewsList = reviewRepository.findAllByFoodId(dailyFood.getFood().getId());
        }

        List<FoodReviewListDto> foodReviewListDtoList = new ArrayList<>();
        for (Reviews reviews : reviewsList){
            Optional<User> optionalUser = userRepository.findById(reviews.getUser().getId());
            List<Comments> commentsList  = commentsRepository.findAllByReviewsId(reviews.getId());
            FoodReviewListDto foodReviewListDto = reviewMapper.toFoodReviewListDto(reviews, optionalUser.get(), commentsList);
            foodReviewListDtoList.add(foodReviewListDto);
        }

        GetFoodReviewResponseDto getFoodReviewResponseDto = reviewMapper.toGetFoodReviewResponseDto(foodReviewListDtoList);


        //등록된 리뷰가 없다면
        if (getFoodReviewResponseDto == null) {
            return "등록된 리뷰가 없습니다.";
        }

        return getFoodReviewResponseDto;
    }
}