package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.OrderItemDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.review.ReviewDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ReviewService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final UserUtil userUtil;
    private final DailyFoodRepository dailyFoodRepository;
    private final OrderItemDailyFoodRepository orderItemDailyFoodRepository;


    @Override
    @Transactional
    public BigInteger createReview(SecurityUser securityUser, ReviewDto reviewDto) {
        // 필요한 정보 가져오기 - 유저, 상품
        User user = userUtil.getUser(securityUser);

        return null;
    }
}
