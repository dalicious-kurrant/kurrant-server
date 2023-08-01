package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.mapper.DailyFoodMapper;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.order.entity.DailyFoodSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.mapper.FoodMapper;
import co.dalicious.domain.order.repository.DailyFoodSupportPriceRepository;
import co.dalicious.domain.order.repository.QOrderItemDailyFoodRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.recommend.dto.UserRecommendWhereData;
import co.dalicious.domain.recommend.entity.UserRecommends;
import co.dalicious.domain.recommend.repository.QUserRecommendRepository;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.ReviewGood;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.LikeMapper;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.*;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.public_api.dto.food.FoodReviewLikeDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
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
    private final ReviewGoodRepository reviewGoodRepository;
    private final LikeMapper likeMapper;
    private final QReviewGoodRepository qReviewGoodRepository;
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;
    private final QKeywordRepository qKeywordRepository;


    @Override
    @Transactional
    public RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate, Integer diningTypeCode) {
        // 유저가 그룹에 속해있는지 확인
        User user = userUtil.getUser(securityUser);

        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        Group group = spot.getGroup();

        List<UserGroup> userGroups = user.getGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group) && v.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));

        if (diningTypeCode != null) {
            DiningType diningType = DiningType.ofCode(diningTypeCode);
            List<DailyFood> dailyFoodList = qDailyFoodRepository.findAllByGroupAndSelectedDateAndDiningType(group, selectedDate, diningType);
            List<DailyFoodDto> dailyFoodDtos = getDailyFoodDtos(user, group, spot, selectedDate, dailyFoodList);
            return RetrieveDailyFoodDto.builder()
                    .dailyFoodDtos(dailyFoodDtos)
                    .build();
        }
        // 유저가 당일날에 해당하는 식사타입이 몇 개인지 확인
        List<RetrieveDailyFoodDto.DiningType> diningTypes = new ArrayList<>();
        RetrieveDailyFoodDto.ServiceDays serviceDays = new RetrieveDailyFoodDto.ServiceDays();
        RetrieveDailyFoodDto.SupportPrice supportPriceDto = new RetrieveDailyFoodDto.SupportPrice();
        List<DailyFoodSupportPrice> dailyFoodSupportPrices = new ArrayList<>();
        if (Hibernate.unproxy(group) instanceof Corporation) {
            dailyFoodSupportPrices = dailyFoodSupportPriceRepository.findAllByUserAndGroupAndServiceDate(user, group, selectedDate);
        }
        for (DiningType diningType : spot.getDiningTypes()) {
            // 이용가능 날짜
            switch (diningType) {
                case MORNING -> {
                    serviceDays.setMorningServiceDays(DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays()));
                    if (Hibernate.unproxy(group) instanceof Corporation)
                        supportPriceDto.setMorningSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, dailyFoodSupportPrices, selectedDate, DiningType.MORNING));
                }
                case LUNCH -> {
                    serviceDays.setLunchServiceDays(DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays()));
                    if (Hibernate.unproxy(group) instanceof Corporation)
                        supportPriceDto.setLunchSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, dailyFoodSupportPrices, selectedDate, DiningType.LUNCH));
                }
                case DINNER -> {
                    serviceDays.setDinnerServiceDays(DaysUtil.serviceDaysToDaysStringList(group.getMealInfo(diningType).getServiceDays()));
                    if (Hibernate.unproxy(group) instanceof Corporation)
                        supportPriceDto.setDinnerSupportPrice(UserSupportPriceUtil.getUsableSupportPrice(spot, dailyFoodSupportPrices, selectedDate, DiningType.DINNER));
                }
            }

            List<LocalTime> deliveryTimes = group.getMealInfo(diningType).getDeliveryTimes();
            List<String> deliveryTimesStr = deliveryTimes.stream()
                    .map(DateUtils::timeToString)
                    .toList();
            RetrieveDailyFoodDto.DiningType diningTypeDto = new RetrieveDailyFoodDto.DiningType(diningType.getCode(), deliveryTimesStr);
            diningTypes.add(diningTypeDto);
        }

        List<DailyFood> dailyFoodList = qDailyFoodRepository.getSellingAndSoldOutDailyFood(group, selectedDate);
        List<DailyFoodDto> dailyFoodDtos = getDailyFoodDtos(user, group, spot, selectedDate, dailyFoodList);

        return RetrieveDailyFoodDto.builder()
                .diningTypes(diningTypes)
                .supportPrice(supportPriceDto)
                .dailyFoodDtos(dailyFoodDtos)
                .serviceDays(serviceDays)
                .build();

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
        DiscountDto discountDto = DiscountDto.getDiscount(dailyFood);
//        DiscountDto discountDto = DiscountDto.getDiscount(dailyFood.getFood());
        return new RetrieveDiscountDto(discountDto);
    }

    @Override
    @Transactional
    public ItemPageableResponseDto<GetFoodReviewResponseDto> getFoodReview(BigInteger dailyFoodId, SecurityUser securityUser, Integer sort, Integer photo, String starFilter, String keywordFilter, OffsetBasedPageRequest pageable) {

        User user = userUtil.getUser(securityUser);

        List<FoodReviewListDto> foodReviewListDtoList = new ArrayList<>();
        List<FoodReviewListDto> sortedFoodReviewListDtoList = new ArrayList<>();

        //유저와 DailyFood 정보 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        //리뷰와 유저정보 가져오기
        Page<Reviews> pageReviews = null;
        List<Reviews> totalReviewsList = reviewRepository.findAllByFoodId(dailyFood.getFood().getId());
        if (totalReviewsList.size() == 0) {
            GetFoodReviewResponseDto getFoodReviewResponseDto = reviewMapper.toGetFoodReviewResponseDto(sortedFoodReviewListDtoList, (double) 0, 0, dailyFood.getFood().getId(), sort,
                    BigInteger.valueOf(0));
            return ItemPageableResponseDto.<GetFoodReviewResponseDto>builder().items(getFoodReviewResponseDto).count(0)
                    .total(0).limit(pageable.getPageSize()).isLast(true).build();
        }

        if ((photo != null && photo != 0) || (starFilter != null && starFilter.length() != 0) || (keywordFilter != null && !keywordFilter.equals(""))) {
            pageReviews = qReviewRepository.findAllByFoodIdSort(dailyFood.getFood().getId(), photo, starFilter, keywordFilter, pageable);

        } else {
            pageReviews = qReviewRepository.findAllByFoodId(dailyFood.getFood().getId(), pageable);
        }

        //대댓글과 별점 추가
        double starAverage;
        int isReview = 0;
        double sumStar = 0;    //별점 계산을 위한 총 별점

        for (Reviews reviews : pageReviews) {
            Optional<User> optionalUser = userRepository.findById(reviews.getUser().getId());
            List<Comments> commentsList = commentsRepository.findAllByReviewsId(reviews.getId());

            //좋아요 눌렀는지 여부 조회
            boolean isGood = false;
            //조회한 유저가 리뷰 작성자인지 여부
            boolean isWriter = optionalUser.get().getId() == user.getId();
            if (isWriter) isReview = 1;
            Optional<ReviewGood> reviewGood = qReviewGoodRepository.foodReviewLikeCheckByUserId(user.getId(), reviews.getId());
            if (reviewGood.isPresent()) isGood = true;
            FoodReviewListDto foodReviewListDto = reviewMapper.toFoodReviewListDto(reviews, optionalUser.get(), commentsList, isGood, isWriter);
            foodReviewListDtoList.add(foodReviewListDto);
        }
        for (Reviews reviews : totalReviewsList) {
            sumStar += reviews.getSatisfaction();
        }

        //기본 정렬
        sortedFoodReviewListDtoList = foodReviewListDtoList.stream().sorted(Comparator.comparing(FoodReviewListDto::getSatisfaction)
                .thenComparing(FoodReviewListDto::getCreateDate)).collect(Collectors.toList());


        Integer totalReviewSize = totalReviewsList.size();
        starAverage = Math.round(sumStar / (double) totalReviewSize * 100) / 100.0;

        //리뷰작성
        BigInteger reviewWrite = null;
        //주문에 대한 리뷰를 작성했는지
        if (isReview == 1) reviewWrite = BigInteger.valueOf(0);
        //주문 한 적 있는지
        OrderItemDailyFood orderItemDailyFood = qOrderItemDailyFoodRepository.findAllByUserAndDailyFood(user.getId(), dailyFood.getFood().getId());
        if (orderItemDailyFood != null) {
            reviewWrite = orderItemDailyFood.getId();
        } else {
            reviewWrite = BigInteger.valueOf(0);
        }

        //주문 기한이 5일이 지났는지(orderItemDailyFood +5일이 오늘보다 작다면)
        if (orderItemDailyFood != null && orderItemDailyFood.getCreatedDateTime().toLocalDateTime().toLocalDate().plusDays(5).isBefore(LocalDate.now())) {
            reviewWrite = BigInteger.valueOf(0);
        }

        GetFoodReviewResponseDto getFoodReviewResponseDto = reviewMapper.toGetFoodReviewResponseDto(sortedFoodReviewListDtoList, starAverage, totalReviewSize, dailyFood.getFood().getId(), sort, reviewWrite);

        return ItemPageableResponseDto.<GetFoodReviewResponseDto>builder().items(getFoodReviewResponseDto).count(pageReviews.getNumberOfElements())
                .total(pageReviews.getTotalPages()).limit(pageable.getPageSize()).isLast(pageReviews.isLast()).build();
    }

    @Override
    @Transactional
    public String foodReviewLike(SecurityUser securityUser, FoodReviewLikeDto foodReviewLikeDto) {
        //유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        Optional<ReviewGood> reviewGood = qReviewGoodRepository.foodReviewLikeCheckByUserId(user.getId(), foodReviewLikeDto.getReviewId());

        if (reviewGood.isPresent()) {
            Optional<Reviews> optionalReviews = reviewRepository.findById(reviewGood.get().getReviewId().getId());
            if (optionalReviews.get().getGood() > 0) { //like가 0보다 클떄만 minus 처리
                qReviewRepository.minusLike(foodReviewLikeDto.getReviewId());
            }
            qReviewRepository.deleteLike(foodReviewLikeDto.getReviewId(), user.getId());
            return "도움이 돼요 취소";
        }

        Optional<Reviews> optionalReviews = reviewRepository.findById(foodReviewLikeDto.getReviewId());

        ReviewGood saveReviewGood = likeMapper.toEntity(user, optionalReviews.get());

        //review_like 테이블에 저장 후 review__review 테이블에 like를 +1 해준다.
        reviewGoodRepository.save(saveReviewGood);
        qReviewRepository.plusLike(foodReviewLikeDto.getReviewId());

        return "도움이 돼요 +1";
    }

    @Override
    public boolean foodReviewLikeCheck(SecurityUser securityUser, BigInteger reviewId) {

        User user = userUtil.getUser(securityUser);

        Optional<ReviewGood> like = qReviewGoodRepository.foodReviewLikeCheckByUserId(user.getId(), reviewId);
        if (like.isEmpty()) return false;

        return true;
    }

    @Override
    public List<String> foodReviewKeyword(BigInteger dailyFoodId) {

        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        return qKeywordRepository.findAllByFoodId(dailyFood.getFood().getId());
    }

    public List<DailyFoodDto> getDailyFoodDtos(User user, Group group, Spot spot, LocalDate selectedDate, List<DailyFood> dailyFoodList) {
        List<DailyFoodDto> dailyFoodDtos = new ArrayList<>();
        Map<DailyFood, Integer> dailyFoodCountMap = orderDailyFoodUtil.getRemainFoodsCount(dailyFoodList);
        List<BigInteger> foodIds = dailyFoodList.stream().map(v -> v.getFood().getId()).collect(Collectors.toList());
        List<UserRecommends> userRecommendList = qUserRecommendRepository.getUserRecommends(
                UserRecommendWhereData.createUserRecommendWhereData(user.getId(), group.getId(), foodIds, selectedDate));
        List<Reviews> reviewList = qReviewRepository.findAllByfoodIds(foodIds);

        for (DailyFood dailyFood : dailyFoodList) {
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
            DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(spot.getId(), dailyFood, discountDto, dailyFoodCountMap.get(dailyFood), userRecommendList, reviewAverage, totalCount, sort);
            dailyFoodDtos.add(dailyFoodDto);
        }

        if (!userRecommendList.isEmpty() && user.getIsMembership()) {
            dailyFoodDtos = dailyFoodDtos.stream()
                    .sorted(Comparator.<DailyFoodDto>comparingInt(dto -> dto.getRank() != null && dto.getRank().equals(1) ? 0 : 1)
                            .thenComparing(DailyFoodDto::getStatus)).toList();
        }
        return dailyFoodDtos.stream().sorted(Comparator.comparing(DailyFoodDto::getSort).reversed()).toList();
    }

    private Integer sortByFoodTag(DailyFood dailyFood) {
        if (!dailyFood.getFood().getFoodTags().isEmpty()) {
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11003))) {    //정찬도시락
                return 10;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11007))) {    //한그릇음식
                return 9;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11004))) {    //산후조리식
                return 8;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11005))) {    //다이어트식
                return 7;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11006))) {    //프로틴식
                return 6;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11002))) {    //샐러드
                return 5;
            }
            if (dailyFood.getFood().getFoodTags().stream().anyMatch(v -> v.getCode().equals(11001))) {    //간편식
                return 3;
            }
        }
        return 0;
    }
}