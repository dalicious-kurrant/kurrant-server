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
import co.dalicious.domain.review.entity.Keyword;
import co.dalicious.domain.review.entity.Like;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.LikeMapper;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.order.mapper.FoodMapper;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.public_api.dto.food.FoodReviewLikeDto;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.model.SecurityUser;
import com.sun.jdi.IntegerValue;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final QLikeRepository qLikeRepository;

    private final QKeywordRepository qKeywordRepository;


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
    public Object getFoodReview(BigInteger dailyFoodId, SecurityUser securityUser, Integer sort, Integer photo, String starFilter) {

        User user = userUtil.getUser(securityUser);

        List<FoodReviewListDto> foodReviewListDtoList = new ArrayList<>();
        List<FoodReviewListDto> sortedFoodReviewListDtoList = new ArrayList<>();

        //유저와 DailyFood 정보 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        //리뷰와 유저정보 가져오기
        List<Reviews> reviewsList = new ArrayList<>();
        List<Reviews> totalReviewsList = new ArrayList<>();
        totalReviewsList = reviewRepository.findAllByFoodId(dailyFood.getFood().getId());


        if ((photo != null && photo != 0) || (starFilter != null && starFilter.length() != 0)){
             reviewsList = qReviewRepository.findAllByfoodIdSort(dailyFood.getFood().getId(), photo, starFilter);
        } else {
            reviewsList = totalReviewsList;
        }

        if (totalReviewsList.size() == 0){
            return reviewMapper.toGetFoodReviewResponseDto(sortedFoodReviewListDtoList, (double) 0, 0, dailyFood.getFood().getId(), sort);
        }

        //대댓글과 별점 추가
        double starEverage;
        double sumStar = 0;    //별점 계산을 위한 총 별점
        for (Reviews reviews : reviewsList){
            Optional<User> optionalUser = userRepository.findById(reviews.getUser().getId());
            List<Comments> commentsList  = commentsRepository.findAllByReviewsId(reviews.getId());


            //좋아요 눌렀는지 여부 조회
            boolean isLike = false;
            //조회한 유저가 리뷰 작성자인지 여부
            boolean isWriter = optionalUser.get().getId() == user.getId() ? true : false;
            Optional<Like> like = qLikeRepository.foodReviewLikeCheckByUserId(user.getId(), reviews.getId());
            if (like.isPresent()) isLike = true;
            FoodReviewListDto foodReviewListDto = reviewMapper.toFoodReviewListDto(reviews, optionalUser.get(), commentsList, isLike, isWriter);
            foodReviewListDtoList.add(foodReviewListDto);
        }
        for (Reviews reviews : totalReviewsList){
            sumStar += reviews.getSatisfaction();
        }

        //기본 정렬

        sortedFoodReviewListDtoList = foodReviewListDtoList.stream().sorted(Comparator.comparing(FoodReviewListDto::getSatisfaction)
                     .thenComparing(FoodReviewListDto::getCreateDate)).collect(Collectors.toList());


        Integer totalReviewSize = totalReviewsList.size();
        starEverage =  Math.round(sumStar / (double) totalReviewSize * 100) / 100.0;

        return reviewMapper.toGetFoodReviewResponseDto(sortedFoodReviewListDtoList, starEverage, totalReviewSize, dailyFood.getFood().getId(), sort);
    }

    @Override
    @Transactional
    public String foodReviewLike(SecurityUser securityUser, FoodReviewLikeDto foodReviewLikeDto) {
        //유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        Optional<Like> like = qLikeRepository.foodReviewLikeCheckByUserId(user.getId(), foodReviewLikeDto.getReviewId());

        if (like.isPresent()){
            Optional<Reviews> optionalReviews = reviewRepository.findById(like.get().getReviewId().getId());
            if (optionalReviews.get().getLike() > 0) { //like가 0보다 클떄만 minus 처리
                qReviewRepository.minusLike(foodReviewLikeDto.getReviewId());
            }
            qReviewRepository.deleteLike(foodReviewLikeDto.getReviewId(), user.getId());
            return "도움이 돼요 취소";
        }

        Optional<Reviews> optionalReviews = reviewRepository.findById(foodReviewLikeDto.getReviewId());

        Like saveLike = likeMapper.toEntity(user, optionalReviews.get());

        //review_like 테이블에 저장 후 review__review 테이블에 like를 +1 해준다.
        likeRepository.save(saveLike);
        qReviewRepository.plusLike(foodReviewLikeDto.getReviewId());

        return "도움이 돼요 +1";
    }

    @Override
    public boolean foodReviewLikeCheck(SecurityUser securityUser, BigInteger reviewId) {

        User user = userUtil.getUser(securityUser);

        Optional<Like> like = qLikeRepository.foodReviewLikeCheckByUserId(user.getId(), reviewId);
        if (like.isEmpty()) return false;

        return true;
    }

    @Override
    public List<String> foodReviewKeyword(BigInteger dailyFoodId) {

        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        return qKeywordRepository.findAllByFoodId(dailyFood.getFood().getId());
    }
}