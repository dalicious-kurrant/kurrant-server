package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.review.dto.ReviewMappingDto;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.review.repository.ReviewRepository;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.review.ReviewDto;
import co.kurrant.app.public_api.mapper.review.ReviewMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ReviewService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final UserUtil userUtil;
    private final DailyFoodRepository dailyFoodRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final QReviewRepository qReviewRepository;

    @Override
    @Transactional
    public BigInteger createReview(SecurityUser securityUser, ReviewDto reviewDto, BigInteger id) {
        // 필요한 정보 가져오기 - 유저, 상품
        User user = userUtil.getUser(securityUser);
        DailyFood dailyFood = dailyFoodRepository.findById(id).orElseThrow();
        Food food = dailyFood.getFood();
        OrderItem orderItem = qOrderDailyFoodRepository.findByUserAndDailyFood(user, dailyFood);

        // content의 최소 글자 수와 최대 글자 수 확인
        if(reviewDto.getContent() == null) {
            throw new ApiException(ExceptionEnum.WRITING_REVIEW);
        } else if(reviewDto.getContent().length() < 11) {
            throw new ApiException(ExceptionEnum.NOT_MATCHED_MIN_OF_CONTENT);
        } else if (reviewDto.getContent().length() > 500) {
            throw new ApiException(ExceptionEnum.OVER_MAX_LIMIT_OF_CONTENT);
        }

        // satisfaction 확인
        if(reviewDto.getSatisfaction() == null || reviewDto.getSatisfaction() < 1) {
            throw new ApiException(ExceptionEnum.NOT_ENOUGH_SATISFACTION);
        }

        // review 생성
        Reviews reviews = reviewMapper.toEntity(reviewDto, user, orderItem, food);

        // 이미 review를 작성한 건인지 검증
        if(qReviewRepository.findByUserAndOrderItem(user, orderItem) != null) {
            throw new ApiException(ExceptionEnum.ALREADY_WRITING_REVIEW);
        }

        // review 저장
        reviewRepository.save(reviews);

        // TODO: 포인트 적립 구현필요

        return reviews.getId();
    }

    @Override
    public List<OrderItem> getOrderItemForReview(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);

        //리뷰 가능한 상품이 있는 지 확인
        List<OrderItemDailyFood> receiptCompleteItem = qOrderDailyFoodRepository.findAllByUserAndOrderStatus(user, OrderStatus.RECEIPT_COMPLETE);
        if(receiptCompleteItem == null || receiptCompleteItem.size() == 0) {
            throw new ApiException(ExceptionEnum.NOT_FOND_ITEM_FOR_REVIEW);
        }

        //리뷰가 가능한 상품인지 확인
        List<OrderItem> itemsForReview = new ArrayList<>();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        for(OrderItem item : receiptCompleteItem) {
            //리뷰 가능일 구하기
            LocalDate completeDate = item.getUpdatedDateTime().toLocalDateTime().toLocalDate();
            LocalDate reviewableDate = completeDate.plusDays(7);

            if(reviewableDate.isAfter(today)) continue;

            itemsForReview.add(item);
        }


        return null;
    }
}
