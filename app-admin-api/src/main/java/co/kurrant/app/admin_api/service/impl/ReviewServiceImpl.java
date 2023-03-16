package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFood;
import co.dalicious.domain.order.entity.QOrderItemDailyFoodGroup;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.QCommentRepository;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.service.ReviewService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final QReviewRepository qReviewRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final QCommentRepository qCommentRepository;
    private final ReviewMapper reviewMapper;
    private final MakersRepository makersRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ReviewAdminResDto> getAllReviews(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
//    public ReviewAdminResDto getAllReviews(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId") == null ? null : BigInteger.valueOf(Integer.parseInt(String.valueOf(parameters.get("makersId"))));
        BigInteger orderItemId = !parameters.containsKey("orderItemId") || parameters.get("orderItemId") == null ? null : BigInteger.valueOf(Integer.parseInt(String.valueOf(parameters.get("orderItemId"))));
        String orderItemName = !parameters.containsKey("orderItemName") || parameters.get("orderItemName") == null ? null : String.valueOf(parameters.get("orderItemName"));
        String writer = !parameters.containsKey("writer") || parameters.get("writer") == null ? null : String.valueOf(parameters.get("writer"));
        Boolean isMakersComment = !parameters.containsKey("isMakersComment") || parameters.get("isMakersComment") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isMakersComment")));
        Boolean isAdminComment = !parameters.containsKey("isAdminComment") || parameters.get("isAdminComment") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isAdminComment")));
        Boolean isReport = !parameters.containsKey("isReport") || parameters.get("isReport") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isReport")));
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate") == null ? null : DateUtils.stringToDate(String.valueOf(parameters.get("startDate")));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate") == null ? null : DateUtils.stringToDate(String.valueOf(parameters.get("endDate")));

        // orderItem 으로 변경하고 관련 주문 상품과 연관 있는 리뷰를 불러온다.
        Page<Reviews> reviewsList = qReviewRepository.findAllByFilter(makersId, orderItemId, orderItemName, writer, startDate, endDate, isReport, isMakersComment, isAdminComment, limit, page, pageable);
//        List<Reviews> reviewsList = qReviewRepository.findAllByFilter(makersId, orderItemId, orderItemName, writer, startDate, endDate, isReport, isMakersComment, isAdminComment);
        List<Makers> makersList = makersRepository.findAll();

        List<ReviewAdminResDto.ReviewList> reviewDtoList = new ArrayList<>();
        Integer count = 0;

        // review dto 만들기
        for(Reviews review : reviewsList) {
            ReviewAdminResDto.ReviewList reviewDto = reviewMapper.toAdminDto(review);

            List<Comments> comments = review.getComments();
            // 달린 코멘트가 하나도 없으면
            if(comments.isEmpty()) {
                reviewDto.setIsMakersComment(false);
                reviewDto.setIsAdminComment(false);
                count++;
            }
            else {
                comments.forEach(comment -> {
                    if (comment instanceof MakersComments) {
                        reviewDto.setIsMakersComment(true);
                    } else if (comment instanceof AdminComments) {
                        reviewDto.setIsAdminComment(true);
                    }
                });
            }
            reviewDtoList.add(reviewDto);
        }

        return ItemPageableResponseDto.<ReviewAdminResDto>builder().items(ReviewAdminResDto.create(makersList, reviewDtoList, count))
                .limit(pageable.getPageSize()).count(reviewsList.getNumberOfElements()).total(reviewsList.getTotalPages()).build();

    }
}
