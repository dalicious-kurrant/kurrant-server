package co.kurrant.app.public_api.service;

import co.dalicious.domain.food.dto.*;
import co.kurrant.app.public_api.dto.food.FoodReviewLikeDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface FoodService {
    RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate, Integer diningType);
    FoodDetailDto getFoodDetail(BigInteger dailyFoodId, SecurityUser securityUser);
    RetrieveDiscountDto getFoodDiscount(BigInteger dailyFoodId);

    Object getFoodReview(BigInteger dailyFoodId, SecurityUser securityUser, Integer sort, Integer photo, Integer starFilter);

    String foodReviewLike(SecurityUser securityUser, FoodReviewLikeDto foodReviewLikeDto);

    boolean foodReviewLikeCheck(SecurityUser securityUser, BigInteger reviewId);

    List<String> foodReviewKeyword(BigInteger dailyFoodId);
}
