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
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.util.PointUtil;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    private final PointUtil pointUtil;

    @Override
    @Transactional
    public void createReview(SecurityUser securityUser, ReviewReqDto reviewDto, List<MultipartFile> fileList) throws IOException {
        // 파일의 최대 갯수는 6개 이다.
        if(fileList != null && fileList.size() > 6) throw new ApiException(ExceptionEnum.REQUEST_OVER_IMAGE_FILE);

        // 필요한 정보 가져오기 - 유저, 상품
        User user = userUtil.getUser(securityUser);
        OrderItem orderItem = qOrderItemRepository.findByUserAndOrderId(user, reviewDto.getOrderItemId());
        List<Reviews> reviewsList = qReviewRepository.findByUserAndOrderItem(user, orderItem);
        if(orderItem == null) throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM_FOR_REVIEW);

        validate(reviewDto.getSatisfaction(), reviewDto.getContent());

        // 찾은 주문 상품이 dailyfood이면
        DailyFood dailyFood = null;
        Food food = null;
        Integer membershipDiscountRate = 0;
        Integer count = 0;
        if(orderItem instanceof OrderItemDailyFood orderItemDailyFood) {
            dailyFood = orderItemDailyFood.getDailyFood();
            food = dailyFood.getFood();
            membershipDiscountRate = orderItemDailyFood.getMembershipDiscountRate();
            count = orderItemDailyFood.getCount();
        }

        // 이미 review를 작성한 건인지 검증
        if(reviewsList != null && !reviewsList.isEmpty()) {
            reviewsList.stream().filter(r -> r.getIsDelete().equals(false))
                    .findFirst()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.ALREADY_WRITING_REVIEW));
        }
        // 리뷰 가능 일이 맞는지 검증
        LocalDate reviewableDate = Objects.requireNonNull(dailyFood).getServiceDate().plusDays(7);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        if(reviewableDate.isBefore(today)) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM_FOR_REVIEW);
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
        qReviewRepository.updateDefault(reviews);

        // 포인트 적립 - 멤버십이 있거나 상품 구매 시점에 멤버십이 있었으면 적립
        if(user.getIsMembership() || membershipDiscountRate != 0) {
            //음식 수량 많큼 포인트 지급
            BigDecimal rewardPoint = pointUtil.findReviewPoint((fileList != null && !fileList.isEmpty()), dailyFood.getFood().getPrice(), count);
            qUserRepository.updateUserPoint(user.getId(), rewardPoint);
            if(!rewardPoint.equals(BigDecimal.ZERO)) pointUtil.createPointHistoryByOthers(user, reviews.getId(), PointStatus.REVIEW_REWARD, rewardPoint);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewableItemResDto getOrderItemForReview(SecurityUser securityUser) throws ParseException {
        User user = userUtil.getUser(securityUser);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        //리뷰 가능한 상품이 있는 지 확인 - 유저 구매했고, 이미 수령을 완료한 식단
        List<OrderItem> receiptCompleteItem = qOrderItemRepository.findByUserAndOrderStatusBeforeToday(user, OrderStatus.RECEIPT_COMPLETE, today);
        List<ReviewableItemResDto.OrderFood> orderFoodList = new ArrayList<>();
        BigDecimal redeemablePoints = BigDecimal.ZERO;
        if(receiptCompleteItem == null || receiptCompleteItem.isEmpty()) { return ReviewableItemResDto.create(orderFoodList, redeemablePoints); }

        // 이미 리뷰가 작성된 아이템 예외
        List<Reviews> reviewsList = qReviewRepository.findAllByUserAndOrderItem(user, receiptCompleteItem);
        List<OrderItem> reviewOrderItem = reviewsList.stream().map(Reviews::getOrderItem).filter(receiptCompleteItem::contains).toList();

        Map<LocalDate, Long> leftDayMap = new HashMap<>();
        MultiValueMap<LocalDate, OrderItemDailyFood> orderItemDailyFoodByServiceDateMap = new LinkedMultiValueMap<>();
        for(OrderItem item : receiptCompleteItem) {

            if(reviewOrderItem.contains(item)) continue;

            if(item instanceof OrderItemDailyFood orderItemDailyFood) {
                LocalDate serviceDate = orderItemDailyFood.getDailyFood().getServiceDate();
                //리뷰 가능일 구하기
                LocalDate reviewableDate = serviceDate.plusDays(5);
                //리뷰 작성 가능일이 이미 지났으면 패스
                if(reviewableDate.isBefore(today)) continue;

                orderItemDailyFoodByServiceDateMap.add(serviceDate, orderItemDailyFood);

                // d-day 구하기
                String reviewableString = DateUtils.localDateToString(reviewableDate);
                String todayString = DateUtils.localDateToString(today);
                long leftDay = DateUtils.calculatedDDay(reviewableString, todayString);

                BigDecimal itemPrice = orderItemDailyFood.getDailyFood().getFood().getPrice();
                int count = orderItemDailyFood.getCount();
                redeemablePoints = redeemablePoints.add(pointUtil.findReviewPoint(true, itemPrice, count));

                leftDayMap.put(serviceDate, leftDay);
            }
        }

        for(LocalDate serviceDate : orderItemDailyFoodByServiceDateMap.keySet()) {
            List<OrderItemDailyFood> orderItemList = orderItemDailyFoodByServiceDateMap.get(serviceDate);

            List<ReviewableItemListDto> reviewableItemListDtoList = new ArrayList<>();
            for(OrderItemDailyFood item : Objects.requireNonNull(orderItemList)) {
                Long leftDay = leftDayMap.get(serviceDate);

                ReviewableItemListDto responseDto = reviewMapper.toDailyFoodResDto(item, leftDay);
                reviewableItemListDtoList.add(responseDto);
            }
            reviewableItemListDtoList = reviewableItemListDtoList.stream().sorted(Comparator.comparing(ReviewableItemListDto::getDiningType).reversed()).toList();

            ReviewableItemResDto.OrderFood orderFood = ReviewableItemResDto.OrderFood.create(reviewableItemListDtoList, serviceDate);
            orderFoodList.add(orderFood);
        }

        orderFoodList = orderFoodList.stream().sorted(Comparator.comparing(ReviewableItemResDto.OrderFood::getServiceDate).reversed()).collect(Collectors.toList());

        return ReviewableItemResDto.create(orderFoodList, redeemablePoints);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewsForUserResDto getReviewsForUser(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);

        // user가 작성한 리뷰 찾기 - 삭제 제외
        List<Reviews> reviews = qReviewRepository.findAllByUser(user);

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
        if(fileList != null && (fileList.size() + updateReqDto.getImages().size()) > 6) throw new ApiException(ExceptionEnum.REQUEST_OVER_IMAGE_FILE);

        User user = userUtil.getUser(securityUser);
        Reviews reviews = qReviewRepository.findByUserAndId(user, reviewsId);
        if(reviews == null) throw new ApiException(ExceptionEnum.NOT_FOUND_REVIEWS);

        validate(updateReqDto.getSatisfaction(), updateReqDto.getContent());

        // 작성일에서 3일이 지났는지 확인 - 리뷰 수정 불가
        LocalDate createdDate = reviews.getCreatedDateTime().toLocalDateTime().toLocalDate();
        LocalDate limitedDate = createdDate.plusDays(3);
        if(createdDate.isAfter(limitedDate)) throw new ApiException(ExceptionEnum.CANNOT_UPDATE_REVIEW);

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

    @Override
    public void deleteReviews(SecurityUser securityUser, BigInteger reviewId) {
        User user = userUtil.getUser(securityUser);
        Reviews reviews = qReviewRepository.findByUserAndId(user, reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.NOT_FOUND_REVIEWS);

        reviews.updatedIsDelete(true);
        reviewRepository.save(reviews);
    }

    private void validate(Integer satisfaction, String content) {
        if (satisfaction == null || satisfaction < 1) {
            throw new ApiException(ExceptionEnum.ENTER_SATISFACTION_LEVEL);
        }
        if (content == null || content.length() < 10 || content.length() >= 501) {
            throw new ApiException(ExceptionEnum.FILL_OUT_THE_REVIEW);
        }
    }
}
