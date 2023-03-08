package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.review.dto.*;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.review.repository.ReviewRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ReviewService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final UserUtil userUtil;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final QReviewRepository qReviewRepository;
    private final QOrderItemRepository qOrderItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageService imageService;
    private final QUserRepository qUserRepository;

    @Override
    @Transactional
    public void createReview(SecurityUser securityUser, ReviewReqDto reviewDto, List<MultipartFile> fileList) throws IOException {
        // 필요한 정보 가져오기 - 유저, 상품
        User user = userUtil.getUser(securityUser);
        OrderItem orderItem = qOrderItemRepository.findByUserAndOrderId(user, reviewDto.getOrderItemId());
        if(orderItem == null) throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM);

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

        // 찾은 주문 상품이 dailyfood이면
        DailyFood dailyFood;
        Food food = null;
        if(orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
            dailyFood = orderItemDailyFood.getDailyFood();
            food = dailyFood.getFood();
        }

        // 이미 review를 작성한 건인지 검증
        if(qReviewRepository.findByUserAndOrderItem(user, orderItem) != null) {
            throw new ApiException(ExceptionEnum.ALREADY_WRITING_REVIEW);
        }

        List<Image> images = new ArrayList<>();
        if(fileList != null && !fileList.isEmpty()) {
            List<ImageResponseDto> imageResponseDtos = imageService.upload(fileList, "reviews");
            images.addAll(Image.toImages(imageResponseDtos));
        }

        // review 생성
        Reviews reviews = reviewMapper.toEntity(reviewDto, user, orderItem, food, images);
        // review 저장
        reviewRepository.save(reviews);

        // 포인트 적립
        // image 있으면 150
        if(fileList != null && !fileList.isEmpty()) qUserRepository.updateUserPoint(user.getId(), BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        // 없으면 50
        qUserRepository.updateUserPoint(user.getId(), null, BigDecimal.valueOf(50));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewableItemResDto getOrderItemForReview(SecurityUser securityUser) throws ParseException {
        User user = userUtil.getUser(securityUser);

        //리뷰 가능한 상품이 있는 지 확인
        List<OrderItem> receiptCompleteItem = qOrderItemRepository.findByUserAndOrderStatus(user, OrderStatus.RECEIPT_COMPLETE);
        List<ReviewableItemListDto> itemsForReview = new ArrayList<>();
        if(receiptCompleteItem == null || receiptCompleteItem.isEmpty()) {
            return ReviewableItemResDto.create(itemsForReview); }

        //리뷰가 가능한 상품인지 확인
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        for(OrderItem item : receiptCompleteItem) {
            //리뷰 가능일 구하기
            LocalDate completeDate = item.getUpdatedDateTime().toLocalDateTime().toLocalDate();
            LocalDate reviewableDate = completeDate.plusDays(7);

            //리뷰 작성 가능일이 이미 지났으면 패스
            if(reviewableDate.isBefore(today)) continue;

            // d-day 구하기
            String reviewableString = DateUtils.localDateToString(reviewableDate);
            String todayString = DateUtils.localDateToString(today);
            long leftDay = DateUtils.calculatedDDay(reviewableString, todayString);

            ReviewableItemListDto responseDto = null;
            if(item instanceof OrderItemDailyFood orderItemDailyFood) {
                responseDto = reviewMapper.toDailyFoodResDto(orderItemDailyFood, leftDay);
            }

            itemsForReview.add(responseDto);
        }

        return ReviewableItemResDto.create(itemsForReview);
    }

    @Override
    @Transactional
    public ReviewsForUserResDto getReviewsForUser(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);

        //user가 작성한 리뷰 찾기
        List<Reviews> reviews = reviewRepository.findByUser(user);
        List<ReviewListDto> reviewListDtos = new ArrayList<>();
        if(reviews == null || reviews.isEmpty()) {
            return ReviewsForUserResDto.create(reviewListDtos);
        }
        for(Reviews review : reviews) {
            ReviewListDto reviewListDto = reviewMapper.toReviewListDto(review);
            reviewListDtos.add(reviewListDto);
        }

        return ReviewsForUserResDto.create(reviewListDtos);
    }
}
