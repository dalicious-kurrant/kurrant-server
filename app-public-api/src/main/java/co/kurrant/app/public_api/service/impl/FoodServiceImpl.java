package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.QSpotRepository;
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
import co.dalicious.domain.order.repository.QDailyFoodSupportPriceRepository;
import co.dalicious.domain.order.repository.QOrderItemDailyFoodRepository;
import co.dalicious.domain.order.util.OrderDailyFoodUtil;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.recommend.dto.UserRecommendByPeriodWhereData;
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
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.public_api.dto.food.DailyFoodResDto;
import co.kurrant.app.public_api.dto.food.FoodReviewLikeDto;
import co.kurrant.app.public_api.mapper.food.PublicDailyFoodMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.util.UserUtil;
import com.mysema.commons.lang.Pair;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final ReviewGoodRepository reviewGoodRepository;
    private final LikeMapper likeMapper;
    private final QReviewGoodRepository qReviewGoodRepository;
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;
    private final QKeywordRepository qKeywordRepository;
    private final QDailyFoodSupportPriceRepository qDailyFoodSupportPriceRepository;
    private final PublicDailyFoodMapper publicDailyFoodMapper;
    private final QSpotRepository qSpotRepository;


    @Override
    @Transactional
    public RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate, Integer diningTypeCode) {
        // 유저가 그룹에 속해있는지 확인
        User user = userUtil.getUser(securityUser);

        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        Group group = spot.getGroup();

        List<UserGroup> userGroups = user.getActiveUserGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group))
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
    public DailyFoodResDto getDailyFoodByPeriod(SecurityUser securityUser, BigInteger spotId, LocalDate startDate, LocalDate endDate) {
        // 유저가 그룹에 속해있는지 확인
        User user = userUtil.getUser(securityUser);

        Spot spot = qSpotRepository.findByIdFetchGroup(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        Group group = spot.getGroup();

        List<UserGroup> userGroups = user.getActiveUserGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));

        // 유저가 당일날에 해당하는 식사타입이 몇 개인지 확인
        List<DailyFood> dailyFoodList = qDailyFoodRepository.getDailyFoodsBetweenServiceDate(startDate, endDate, group);
        List<DailyFoodSupportPrice> dailyFoodSupportPriceList = group instanceof Corporation
                ? qDailyFoodSupportPriceRepository.findAllUserSupportPriceHistoryBySpotBetweenServiceDate(user, group, startDate, endDate)
                : new ArrayList<>();
        Map<DailyFood, Integer> dailyFoodCountMap = orderDailyFoodUtil.getRemainFoodsCount(dailyFoodList);

        Set<BigInteger> foodIds = dailyFoodList.stream().map(v -> v.getFood().getId()).collect(Collectors.toSet());
        List<UserRecommends> userRecommendList = qUserRecommendRepository.getUserRecommends(
                new UserRecommendByPeriodWhereData(user.getId(), group.getId(), foodIds, new PeriodDto(startDate, endDate)));

        Map<BigInteger, Pair<Double, Long>> reviewMap = qReviewRepository.getStarAverage(foodIds);

        return publicDailyFoodMapper.toDailyFoodResDto(startDate, endDate, dailyFoodList, group, spot, dailyFoodSupportPriceList, dailyFoodCountMap, userRecommendList, reviewMap, user);
    }

    @Override
    @Transactional
    public FoodDetailDto getFoodDetail(BigInteger dailyFoodId, SecurityUser securityUser) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );

        String lastOrderTime = getLastOrderTime(dailyFood);

        Spot spot = user.getDefaultUserSpot().getSpot();
        DiscountDto discountDto = OrderUtil.checkMembershipAndGetDiscountDto(user, dailyFood.getGroup(), spot, dailyFood);
        FoodDetailDto foodDetailDto = foodMapper.toDto(dailyFood, discountDto);
        foodDetailDto.setCapacity(orderDailyFoodUtil.getRemainFoodCount(dailyFood).getRemainCount());

        DiscountDto discountedDto = DiscountDto.getDiscount(dailyFood);
        foodDetailDto.setTotalDiscountRate(discountedDto.getPrice().subtract(discountedDto.getMembershipDiscountPrice()).subtract(discountedDto.getMakersDiscountPrice()).subtract(discountedDto.getPeriodDiscountPrice()).intValue());
        foodDetailDto.setTotalDiscountedPrice(discountedDto.getPrice().subtract(discountedDto.getPrice().subtract(discountedDto.getMembershipDiscountPrice()).subtract(discountedDto.getMakersDiscountPrice()).subtract(discountedDto.getPeriodDiscountPrice())).divide(discountDto.getPrice(), 3).multiply(BigDecimal.valueOf(100L)));
        foodDetailDto.setLastOrderTime(lastOrderTime);
        foodDetailDto.setIsMembership(user.getIsMembership() || ((Group) Hibernate.unproxy(dailyFood.getGroup()) instanceof Corporation corporation && corporation.getIsMembershipSupport()));

        return foodDetailDto;
    }

    private String getLastOrderTime(DailyFood dailyFood) {
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

    @Override
    @Transactional
    public RetrieveDiscountDto getFoodDiscount(BigInteger dailyFoodId) {
        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );
        // TODO: 식단에 가격 업데이트 적용이 되는 시점부터 주석 해제
        DiscountDto discountDto = DiscountDto.getDiscount(dailyFood);
        return new RetrieveDiscountDto(discountDto);
    }

    @Override
    @Transactional
    public ItemPageableResponseDto<GetFoodReviewResponseDto> getFoodReview(BigInteger dailyFoodId, SecurityUser securityUser, Integer sort, Integer photo, String starFilter, String keywordFilter, OffsetBasedPageRequest pageable) {

        User user = userUtil.getUser(securityUser);

        List<FoodReviewListDto> foodReviewListDtoList = new ArrayList<>();
        List<FoodReviewListDto> sortedFoodReviewListDtoList = new ArrayList<>();
        List<String> keywords = new ArrayList<>();
        Map<Integer, Integer> stars = new HashMap<>();

        //유저와 DailyFood 정보 가져오기
        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        //리뷰와 유저정보 가져오기
        Page<Reviews> pageReviews = null;
        //Total 리뷰 조회 (사장님에게만보이기는 제외)
        List<Reviews> totalReviewsList = qReviewRepository.findAllByfoodIdsAndForMakers(dailyFood.getFood().getId());
        if (totalReviewsList.size() == 0) {
            GetFoodReviewResponseDto getFoodReviewResponseDto = reviewMapper.toGetFoodReviewResponseDto(sortedFoodReviewListDtoList, (double) 0, 0, dailyFood.getFood().getId(), sort,
                    BigInteger.valueOf(0), keywords, stars);
            return ItemPageableResponseDto.<GetFoodReviewResponseDto>builder().items(getFoodReviewResponseDto).count(0)
                    .total(0).limit(pageable.getPageSize()).isLast(true).build();
        }

        pageReviews = qReviewRepository.findAllByFoodIdSort(dailyFood.getFood().getId(), photo, starFilter, keywordFilter, pageable, sort);


        //대댓글과 별점 추가
        int isReview = 0;
        int sumStar = 0;    //별점 계산을 위한 총 별점
        if (totalReviewsList.stream().anyMatch(v -> v.getUser().getId().equals(user.getId()))) isReview = 1;

        for (Reviews reviews : pageReviews) {
            Optional<User> optionalUser = userRepository.findById(reviews.getUser().getId());
            List<Comments> commentsList = commentsRepository.findAllByReviewsId(reviews.getId());

            //좋아요 눌렀는지 여부 조회
            boolean isGood = false;
            //조회한 유저가 리뷰 작성자인지 여부
            boolean isWriter = reviews.getUser().getId().equals(user.getId());
            Optional<ReviewGood> reviewGood = qReviewGoodRepository.foodReviewLikeCheckByUserId(user.getId(), reviews.getId());
            if (reviewGood.isPresent()) isGood = true;
            FoodReviewListDto foodReviewListDto = reviewMapper.toFoodReviewListDto(reviews, optionalUser.get(), commentsList, isGood, isWriter);
            foodReviewListDtoList.add(foodReviewListDto);
        }
        for (Reviews reviews : totalReviewsList) {
            sumStar += reviews.getSatisfaction();
        }

        Integer totalReviewSize = totalReviewsList.size();
        Double starAverage = Math.round(sumStar / (double) totalReviewSize * 100) / 100.0;

        //리뷰작성
        BigInteger reviewWrite = null;
        //주문 한 적 있고 5일지 지나지 않았다면 orderItemDailyFoodId 반환
        OrderItemDailyFood orderItemDailyFood = qOrderItemDailyFoodRepository.findAllByUserAndDailyFood(user.getId(), dailyFood.getId());
        if (orderItemDailyFood != null && isReview != 1) {
            reviewWrite = orderItemDailyFood.getId();
        } else {
            reviewWrite = BigInteger.valueOf(0);
        }

        keywords = qKeywordRepository.findAllByFoodId(dailyFood.getFood().getId());

        GetFoodReviewResponseDto getFoodReviewResponseDto = reviewMapper.toGetFoodReviewResponseDto(foodReviewListDtoList, starAverage, totalReviewSize, dailyFood.getFood().getId(), sort, reviewWrite, keywords, getStarRate(totalReviewsList));

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

        String lastOrderTime = null;

        for (DailyFood dailyFood : dailyFoodList) {
            int sumStar = 0;

            if (dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime() != null){
                lastOrderTime = dailyFood.getGroup().getMealInfo(dailyFood.getDiningType()).getLastOrderTime().dayAndTimeToStringByDate(dailyFood.getServiceDate());
            }

            if (dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime() != null){
                lastOrderTime = dailyFood.getFood().getMakers().getMakersCapacity(dailyFood.getDiningType()).getLastOrderTime().dayAndTimeToStringByDate(dailyFood.getServiceDate());
            }

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
            DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(spot.getId(), dailyFood, discountDto, dailyFoodCountMap.get(dailyFood), userRecommendList, reviewAverage, totalCount, sort, lastOrderTime);
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

    private Map<Integer, Integer> getStarRate(List<Reviews> reviewsList) {
        Map<Integer, Integer> starCountMap = new ConcurrentHashMap<>();
        starCountMap.put(1, 0);
        starCountMap.put(2, 0);
        starCountMap.put(3, 0);
        starCountMap.put(4, 0);
        starCountMap.put(5, 0);
        for (Reviews reviews : reviewsList){
            starCountMap.merge(reviews.getSatisfaction(), 1, (k, v) -> starCountMap.get(reviews.getSatisfaction())+1);
        }
        return starCountMap;
    }
}