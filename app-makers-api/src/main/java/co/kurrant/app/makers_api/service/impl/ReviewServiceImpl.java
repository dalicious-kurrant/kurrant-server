package co.kurrant.app.makers_api.service.impl;

import co.dalicious.client.alarm.dto.PushRequestDto;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewMakersResDto;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.CommentsRepository;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.review.repository.ReviewRepository;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ReviewService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final QReviewRepository qReviewRepository;
    private final ReviewMapper reviewMapper;
    private final CommentsRepository commentsRepository;
    private final UserUtil userUtil;
    private final PushUtil pushUtil;
    private final PushService pushService;
    private final PushAlarmHashRepository pushAlarmHashRepository;

    @Override
    @Transactional
    public void createMakersComment(BigInteger reviewId, CommentReqDto reqDto) {
        Reviews reviews = qReviewRepository.findById(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);


        List<Comments> commentsList = reviews.getComments();
        for(Comments comments : commentsList) {
            if(comments instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                throw new ApiException(ExceptionEnum.ALREADY_WRITE_COMMENT_REVIEW);
            }
        }

        MakersComments comments = reviewMapper.toMakersComment(reqDto, reviews);
        commentsRepository.save(comments);

        // 댓글 생성 푸시알림
        BigInteger userId = reviews.getUser().getId();
        Map<String, Set<BigInteger>> userIdsMap = Collections.singletonMap("userIds", new HashSet<>(Collections.singletonList(userId)));

        PushRequestDto pushRequestDto = pushUtil.sendToType(userIdsMap, PushCondition.REVIEW_GET_COMMENT, reviews.getId(), "reviewId", null);
        pushService.sendToPush(pushRequestDto);

        PushAlarmHash pushAlarmHash = PushAlarmHash.builder()
                .title(pushRequestDto.getTitle())
                .isRead(false)
                .message(pushRequestDto.getMessage())
                .userId(userId)
                .type(AlarmType.REVIEW.getAlarmType())
                .reviewId(reviews.getId())
                .build();
        pushAlarmHashRepository.save(pushAlarmHash);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ReviewMakersResDto> getUnansweredReview(SecurityUser securityUser, String foodName, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Makers makers = userUtil.getMakers(securityUser);

        // 메이커스 댓글이 없는 리뷰만 조회 - 삭제 제외, 신고 제외
        Page<Reviews> reviewsList = qReviewRepository.findAllByMakersExceptMakersComment(makers, foodName, limit, page, pageable);
        ReviewMakersResDto reviewMakersResDto = new ReviewMakersResDto();
        List<ReviewMakersResDto.ReviewListDto> reviewListDtoList = new ArrayList<>();
        if(reviewsList.isEmpty()) {
            reviewMakersResDto.setCount(0);
            reviewMakersResDto.setReviewListDtoList(reviewListDtoList);
            return ItemPageableResponseDto.<ReviewMakersResDto>builder()
                    .items(reviewMakersResDto).limit(pageable.getPageSize())
                    .total(reviewsList.getTotalPages()).count(reviewsList.getNumberOfElements())
                    .build();
        }

        for(Reviews reviews : reviewsList) {
            ReviewMakersResDto.ReviewListDto reviewListDto = reviewMapper.toMakersReviewListDto(reviews);
            reviewListDtoList.add(reviewListDto);
        }

        reviewMakersResDto.setCount((int) qReviewRepository.countReviewByMakers(makers, true));
        reviewMakersResDto.setReviewListDtoList(reviewListDtoList);

        return ItemPageableResponseDto.<ReviewMakersResDto>builder()
                .items(reviewMakersResDto).limit(pageable.getPageSize())
                .total(reviewsList.getTotalPages()).count(reviewsList.getNumberOfElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<ReviewMakersResDto> getAllReview(SecurityUser securityUser, String foodName, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Makers makers = userUtil.getMakers(securityUser);

        // 메이커스 댓글이 없는 리뷰만 조회 - 삭제, 신고 제외
        Page<Reviews> reviewsList = qReviewRepository.findAllByMakers(makers, foodName, limit, page, pageable);

        ReviewMakersResDto reviewMakersResDto = new ReviewMakersResDto();
        List<ReviewMakersResDto.ReviewListDto> reviewListDtoList = new ArrayList<>();
        if(reviewsList.isEmpty()) {
            reviewMakersResDto.setCount(0);
            reviewMakersResDto.setReviewListDtoList(reviewListDtoList);
            return ItemPageableResponseDto.<ReviewMakersResDto>builder()
                    .items(reviewMakersResDto).limit(pageable.getPageSize())
                    .total(reviewsList.getTotalPages()).count(reviewsList.getNumberOfElements())
                    .build();
        }

        for(Reviews reviews : reviewsList) {
            ReviewMakersResDto.ReviewListDto reviewListDto = reviewMapper.toMakersReviewListDto(reviews);
            reviewListDtoList.add(reviewListDto);
        }

        reviewMakersResDto.setCount((int) qReviewRepository.countReviewByMakers(makers, false));
        reviewMakersResDto.setReviewListDtoList(reviewListDtoList);

        return ItemPageableResponseDto.<ReviewMakersResDto>builder()
                .items(reviewMakersResDto).limit(pageable.getPageSize())
                .total(reviewsList.getTotalPages()).count(reviewsList.getNumberOfElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewMakersResDto.ReviewDetail getReviewDetail(BigInteger reviewId) {
        Reviews reviews = qReviewRepository.findById(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);

        MultiValueMap<LocalDate, Integer> dateAndScore = qReviewRepository.getReviewScoreMap((Food) Hibernate.unproxy(reviews.getFood()));

        return reviewMapper.toMakersReviewDetails(reviews, dateAndScore);
    }

    @Override
    @Transactional
    public void updateMakersComment(BigInteger commentId, CommentReqDto reqDto) {
        Comments comments = commentsRepository.findById(commentId).orElseThrow(() -> new ApiException(ExceptionEnum.MAKERS_COMMENT_NOT_FOUND));

        if(comments instanceof MakersComments makersComments) {
            makersComments.updateComment(reqDto.getContent());
        }
    }

//    @Override
//    @Transactional
//    public void deleteMakersComment(BigInteger commentId) {
//        Comments comments = commentsRepository.findById(commentId).orElseThrow(() -> new ApiException(ExceptionEnum.MAKERS_COMMENT_NOT_FOUND));
//
//        if(comments instanceof MakersComments makersComments) {
//            makersComments.updateIsDelete(true);
//        }
//    }

    @Override
    @Transactional
    public void reportReviews(BigInteger reviewId) {
        Reviews reviews = qReviewRepository.findById(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);
        if(reviews.getIsReports()) throw new ApiException(ExceptionEnum.ALREADY_REPORTED_REVIEW);

        reviews.updateIsReport(true);
    }
}
