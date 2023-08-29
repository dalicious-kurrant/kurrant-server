package co.kurrant.app.public_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.food.dto.*;
import co.kurrant.app.public_api.dto.food.DailyFoodByDateDto;
import co.kurrant.app.public_api.dto.food.DailyFoodResDto;
import co.kurrant.app.public_api.dto.food.FoodReviewLikeDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface FoodService {
    RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate, Integer diningType);
    DailyFoodResDto getDailyFoodByPeriod(SecurityUser securityUser, BigInteger spotId, LocalDate startDate, LocalDate endDate);
    DailyFoodByDateDto getDailyFoodByPeriodAndServiceDate(SecurityUser securityUser, BigInteger spotId, LocalDate startDate, LocalDate endDate);
    FoodDetailDto getFoodDetail(BigInteger dailyFoodId, SecurityUser securityUser);
    RetrieveDiscountDto getFoodDiscount(BigInteger dailyFoodId);

    ItemPageableResponseDto<GetFoodReviewResponseDto> getFoodReview(BigInteger dailyFoodId, SecurityUser securityUser, Integer sort, Integer photo, String starFilter, String keywordFilter, OffsetBasedPageRequest pageable);

    String foodReviewLike(SecurityUser securityUser, FoodReviewLikeDto foodReviewLikeDto);

    boolean foodReviewLikeCheck(SecurityUser securityUser, BigInteger reviewId);

    List<String> foodReviewKeyword(BigInteger dailyFoodId);
}
