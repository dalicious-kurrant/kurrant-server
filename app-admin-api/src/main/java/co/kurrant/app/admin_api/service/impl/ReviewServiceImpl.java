package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.sse.SseService;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.repository.MakersRepository;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import co.dalicious.domain.review.dto.ReviewKeywordSaveReqDto;
import co.dalicious.domain.review.entity.*;
import co.dalicious.domain.review.mapper.KeywordMapper;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.CommentsRepository;
import co.dalicious.domain.review.repository.KeywordRepository;
import co.dalicious.domain.review.repository.QKeywordRepository;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.service.ReviewService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final QReviewRepository qReviewRepository;
    private final CommentsRepository commentsRepository;
    private final ReviewMapper reviewMapper;
    private final MakersRepository makersRepository;
    private final PushUtil pushUtil;
    private final PushService pushService;
    private final KeywordRepository keywordRepository;
    private final KeywordMapper keywordMapper;
    private final QKeywordRepository qKeywordRepository;
    private final FoodRepository foodRepository;
    private SseService sseService;

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ReviewAdminResDto> getAllReviews(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        BigInteger makersId = !parameters.containsKey("makersId") || parameters.get("makersId") == null ? null : BigInteger.valueOf(Integer.parseInt(String.valueOf(parameters.get("makersId"))));
        String orderCode = !parameters.containsKey("orderCode") || parameters.get("orderCode") == null ? null : String.valueOf(parameters.get("orderCode"));
        String orderItemName = !parameters.containsKey("orderItemName") || parameters.get("orderItemName") == null ? null : String.valueOf(parameters.get("orderItemName"));
        String writer = !parameters.containsKey("writer") || parameters.get("writer") == null ? null : String.valueOf(parameters.get("writer"));
        Boolean isMakersComment = !parameters.containsKey("isMakersComment") || parameters.get("isMakersComment") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isMakersComment")));
        Boolean isAdminComment = !parameters.containsKey("isAdminComment") || parameters.get("isAdminComment") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isAdminComment")));
        Boolean isReport = !parameters.containsKey("isReport") || parameters.get("isReport") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("isReport")));
        Boolean forMakers = !parameters.containsKey("forMakers") || parameters.get("forMakers") == null ? null : Boolean.valueOf(String.valueOf(parameters.get("forMakers")));
        LocalDate startDate = !parameters.containsKey("startDate") || parameters.get("startDate") == null ? null : DateUtils.stringToDate(String.valueOf(parameters.get("startDate")));
        LocalDate endDate = !parameters.containsKey("endDate") || parameters.get("endDate") == null ? null : DateUtils.stringToDate(String.valueOf(parameters.get("endDate")));

        //서비스 날 기준으로 작성자, 주문번호, 주문 상품 이름, 작성자, 메이커스 댓글 여부, 관리자 댓글 여부, 신고여부, 메이커스로 필터링한 리뷰 조회 - 삭제 포함
        Page<Reviews> reviewsList = qReviewRepository.findAllByFilter(makersId, orderCode, orderItemName, writer, startDate, endDate, isReport, forMakers, isMakersComment, isAdminComment, limit, page, pageable);
        List<Makers> makersList = makersRepository.findAll();
        long count = qReviewRepository.pendingReviewCount();

        List<ReviewAdminResDto.ReviewList> reviewDtoList = new ArrayList<>();

        // review dto 만들기
        for(Reviews review : reviewsList) {
            ReviewAdminResDto.ReviewList reviewDto = reviewMapper.toAdminDto(review);

            List<Comments> comments = review.getComments();
            // 달린 코멘트가 하나도 없으면
            reviewDto.setIsMakersComment(false);
            reviewDto.setIsAdminComment(false);
            if(!comments.isEmpty()) {
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

    @Override
    @Transactional(readOnly = true)
    public ReviewAdminResDto.ReviewDetail getReviewsDetail(BigInteger reviewId) {
        Reviews reviews = qReviewRepository.findById(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);

        return reviewMapper.toReviewDetails(reviews);
    }

    @Override
    @Transactional
    public void createAdminComment(CommentReqDto reqDto, BigInteger reviewId) {
        Reviews reviews = qReviewRepository.findByIdExceptedDelete(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);

        List<Comments> commentsList = reviews.getComments();
        for(Comments comments : commentsList) {
            if(comments instanceof AdminComments adminComments && !adminComments.getIsDelete()) {
                throw new ApiException(ExceptionEnum.ALREADY_WRITE_COMMENT_REVIEW);
            }
        }

        AdminComments adminComments = reviewMapper.toAdminComment(reqDto, reviews);
        commentsRepository.save(adminComments);
        sseService.send(reviews.getUser().getId(), 8, null, null, adminComments.getId());

        // 댓글 생성 푸시알림
        PushRequestDtoByUser pushRequestDtoByUser = pushUtil.getPushRequest(reviews.getUser(), PushCondition.REVIEW_GET_COMMENT, null);
        if(pushRequestDtoByUser != null) {
            pushService.sendToPushByKey(List.of(pushRequestDtoByUser), Collections.singletonMap("reviewId", String.valueOf(reviews.getId())));
            sseService.send(reviews.getUser().getId(), 6, null, null, null);
            pushUtil.savePushAlarmHash(pushRequestDtoByUser.getTitle(), pushRequestDtoByUser.getMessage(), reviews.getUser().getId(), AlarmType.REVIEW, reviews.getId());
        }
    }

    @Override
    @Transactional
    public void updateAdminComment(CommentReqDto reqDto, BigInteger commentId) {
        Comments comments = commentsRepository.findById(commentId).orElseThrow(() -> new ApiException(ExceptionEnum.ADMIN_COMMENT_NOT_FOUND));

        if(comments instanceof AdminComments adminComments) {
            adminComments.updateAdminComment(reqDto.getContent());
        }
    }

    @Override
    @Transactional
    public void deleteReview(BigInteger reviewId) {
        Reviews reviews = qReviewRepository.findByIdExceptedDelete(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);

        reviews.updatedIsDelete(true);
    }

    @Override
    @Transactional
    public void reportReview(BigInteger reviewId) {
        // 삭제된 리뷰 제외하기
        Reviews reviews = qReviewRepository.findByIdExceptedDelete(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);
        if(reviews.getIsReports()) throw new ApiException(ExceptionEnum.ALREADY_REPORTED_REVIEW);

        reviews.updateIsReport(true);
    }

    @Override
    @Transactional
    public void deleteComment(BigInteger commentId) {
        Comments comments = commentsRepository.findById(commentId).orElseThrow(() -> new ApiException(ExceptionEnum.MAKERS_COMMENT_NOT_FOUND));

        if(comments instanceof MakersComments makersComments) {
            makersComments.updateIsDelete(true);
        }
        else if(comments instanceof AdminComments adminComments) {
            adminComments.updateIsDelete(true);
        }
    }

    @Override
    public List<String> foodReviewKeyword(BigInteger foodId) {
        return qKeywordRepository.findAllByFoodId(foodId);
    }

    @Override
    @Transactional
    public void reviewKeywordSave(ReviewKeywordSaveReqDto keywordDto) {

        Food food = foodRepository.findById(keywordDto.getFoodId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        keywordRepository.deleteAllByFoodId(food.getId());

        for (String name : keywordDto.getNames()) {
            Long keywordCount1 = qReviewRepository.findKeywordCount(name, food.getId());
            int keywordCount = keywordCount1.intValue();
            Keyword keyword = keywordMapper.toEntity(name, keywordCount, food);
            keywordRepository.save(keyword);
        }




    }
}
