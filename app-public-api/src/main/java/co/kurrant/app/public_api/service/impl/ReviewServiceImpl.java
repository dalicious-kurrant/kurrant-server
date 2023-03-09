package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
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
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
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
    private final ImageService imageService;
    private final QUserRepository qUserRepository;

    @Override
    @Transactional
    public void createReview(SecurityUser securityUser, ReviewReqDto reviewDto, List<MultipartFile> fileList) throws IOException {
        // 파일의 최대 갯수는 6개 이다.
        if(fileList != null && fileList.size() > 6) throw new ApiException(ExceptionEnum.MAX_LIMIT_IMAGE_FILE);

        // 필요한 정보 가져오기 - 유저, 상품
        User user = userUtil.getUser(securityUser);
        OrderItem orderItem = qOrderItemRepository.findByUserAndOrderId(user, reviewDto.getOrderItemId());
        if(orderItem == null) throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM_FOR_REVIEW);

        validate(reviewDto.getSatisfaction(), reviewDto.getContent());

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
    @Transactional(readOnly = true)
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

    @Override
    @Transactional
    public void updateReviews(SecurityUser securityUser, List<MultipartFile> fileList, ReviewUpdateReqDto updateReqDto, BigInteger reviewsId) throws IOException {
        // 파일의 최대 갯수는 6개 이다.
        if(fileList != null && (fileList.size() + updateReqDto.getImages().size()) > 6) throw new ApiException(ExceptionEnum.MAX_LIMIT_IMAGE_FILE);

        User user = userUtil.getUser(securityUser);
        Reviews reviews = qReviewRepository.findByUserAndId(user, reviewsId);
        if(reviews == null) throw new ApiException(ExceptionEnum.NOT_FOUND_REVIEWS);

        validate(updateReqDto.getSatisfaction(), updateReqDto.getContent());

        // 작성일에서 3일이 지났는지 확인 - 리뷰 수정 불가
        LocalDate createdDate = reviews.getCreatedDateTime().toLocalDateTime().toLocalDate();
        LocalDate limitedDate = createdDate.plusDays(3);
        if(createdDate.isAfter(limitedDate)) throw new ApiException(ExceptionEnum.CAN_NOT_UPDATE_REVIEW);

        // 이미지가 삭제되었다면 S3에서도 삭제
        List<Image> imageList = new ArrayList<>();
        List<String> requestImage = updateReqDto.getImages();
        if(requestImage != null && requestImage.size() != reviews.getImages().size()) {
            List<Image> deleteImages = reviews.getImages();
            List<Image> selectedImages = reviews.getImages().stream()
                    .filter(v -> requestImage.contains(v.getLocation()))
                    .toList();
            deleteImages.removeAll(selectedImages);
            if(!deleteImages.isEmpty()) {
                for (Image image : deleteImages) {
                    imageService.delete(image.getPrefix());
                }
            }
            imageList.addAll(selectedImages);
        } else {
            imageList.addAll(reviews.getImages());
        }

        if(fileList != null && !fileList.isEmpty()) {
            List<ImageResponseDto> imageResponseDtos = imageService.upload(fileList, "reviews");
            imageList.addAll(Image.toImages(imageResponseDtos));
        }

        reviews.updatedReviews(updateReqDto, imageList);
    }

    private void validate(Integer satisfaction, String content) {
        if (satisfaction == null || satisfaction < 1) {
            throw new ApiException(ExceptionEnum.ENTER_SATISFACTION_LEVEL);
        }
        if (content == null || content.length() < 11 || content.length() >= 500) {
            throw new ApiException(ExceptionEnum.FILL_OUT_THE_REVIEW);
        }
    }
}
