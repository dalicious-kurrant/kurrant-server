package co.dalicious.domain.review.mapper;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.dto.FoodReviewListDto;
import co.dalicious.domain.food.dto.GetFoodReviewResponseDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.review.dto.*;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Math.class, UserRepository.class})
public interface ReviewMapper {

    @Mapping(source = "imageList", target = "images")
    @Mapping(source = "reviewDto.content", target = "content")
    @Mapping(source = "reviewDto.content", target = "contentOrigin")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfaction")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfactionOrigin")
    @Mapping(source = "reviewDto.forMakers", target = "forMakers")
    Reviews toEntity(ReviewReqDto reviewDto, User user, OrderItem orderItem, Food food, List<Image> imageList);

    default ReviewableItemListDto toDailyFoodResDto(OrderItemDailyFood orderItemDailyFood, String reviewDDAy) {
        ReviewableItemListDto reviewableItemListDto = new ReviewableItemListDto();

        reviewableItemListDto.setOrderItemId(orderItemDailyFood.getId());
        reviewableItemListDto.setDailyFoodId(orderItemDailyFood.getDailyFood().getId());
        reviewableItemListDto.setDiningType(orderItemDailyFood.getDailyFood().getDiningType().getDiningType());
        reviewableItemListDto.setImageLocation(getLocation(orderItemDailyFood.getDailyFood().getFood().getImages()));
        reviewableItemListDto.setMakersName(orderItemDailyFood.getDailyFood().getFood().getMakers().getName());
        reviewableItemListDto.setFoodName(orderItemDailyFood.getDailyFood().getFood().getName());
        reviewableItemListDto.setFoodDescription(orderItemDailyFood.getDailyFood().getFood().getDescription());
        reviewableItemListDto.setFoodCount(orderItemDailyFood.getCount());
        reviewableItemListDto.setReviewDDay(reviewDDAy);
        return reviewableItemListDto;
    }

    @Mapping(source = "reviews.id", target = "reviewId")
    @Mapping(source = "reviews.images", target = "imageLocation", qualifiedByName = "getImagesLocations")
    @Mapping(source = "reviews.content", target = "content")
    @Mapping(source = "reviews.satisfaction", target = "satisfaction")
    @Mapping(source = "reviews.createdDateTime", target = "createDate")
    @Mapping(source = "reviews.updatedDateTime", target = "updateDate")
    @Mapping(source = "reviews.forMakers", target = "forMakers")
    @Mapping(source = "reviews.orderItem", target = "makersName", qualifiedByName = "getMakersName")
    @Mapping(source = "reviews.orderItem", target = "itemName", qualifiedByName = "getItemName")
    @Mapping(source = "reviews.comments", target = "commentList", qualifiedByName = "setCommentList")
    ReviewListDto toReviewListDto(Reviews reviews);


    @Mapping(source = "isGood", target = "isGood")
    @Mapping(source = "isWriter", target = "isWriter")
    @Mapping(source = "reviews.images", target = "imageLocation", qualifiedByName = "getImagesLocations")
    @Mapping(source = "reviews.comments", target = "commentList", qualifiedByName = "setCommentList2")
    @Mapping(source = "reviews.createdDateTime", target = "createDate", qualifiedByName = "getCreateDate")
    @Mapping(source = "reviews.updatedDateTime", target = "updateDate", qualifiedByName = "getCreateDate")
    @Mapping(source = "reviews.satisfaction", target = "satisfaction")
    @Mapping(source = "user.nickname", target = "userName")
    @Mapping(source = "reviews.good", target = "good")
    @Mapping(source = "reviews.id", target = "reviewId")
    FoodReviewListDto toFoodReviewListDto(Reviews reviews, User user, List<Comments> commentsList, boolean isGood, boolean isWriter);

    @Named("getCreateDate")
    default String getCreateDate(Timestamp createdDateTime) {
        return createdDateTime.toString().substring(0, 10);
    }

    @Named("setCommentList")
    default List<ReviewListDto.Comment> setCommentList(List<Comments> commentsList) {
        List<ReviewListDto.Comment> commentList = new ArrayList<>();

        if (commentsList.isEmpty()) return commentList;

        commentsList = commentsList.stream().sorted(Comparator.comparing(Comments::getCreatedDateTime)).toList();

        for (Comments comments : commentsList) {
            ReviewListDto.Comment comment = new ReviewListDto.Comment();
            if (comments instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                comment.setWriter(makersComments.getReviews().getFood().getMakers().getName());
                comment.setContent(makersComments.getContent());
                comment.setCreateDate(DateUtils.toISOLocalDate(makersComments.getCreatedDateTime()));
                comment.setUpdateDate(DateUtils.toISOLocalDate(makersComments.getUpdatedDateTime()));
                commentList.add(comment);
            } else if (comments instanceof AdminComments adminComments && !adminComments.getIsDelete()) {
                comment.setWriter("admin");
                comment.setContent(adminComments.getContent());
                comment.setCreateDate(DateUtils.toISOLocalDate(adminComments.getCreatedDateTime()));
                comment.setUpdateDate(DateUtils.toISOLocalDate(adminComments.getUpdatedDateTime()));
                commentList.add(comment);
            }
        }
        return commentList;
    }

    @Named("setCommentList2")
    default List<FoodReviewListDto.Comment> setCommentList2(List<Comments> commentsList) {
        List<FoodReviewListDto.Comment> commentList = new ArrayList<>();

        if (commentsList.isEmpty()) return commentList;

        commentsList = commentsList.stream().sorted(Comparator.comparing(Comments::getCreatedDateTime)).toList();

        for (Comments comments : commentsList) {
            FoodReviewListDto.Comment comment = new FoodReviewListDto.Comment();
            if (comments instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                comment.setWriter(makersComments.getReviews().getFood().getMakers().getName());
                comment.setContent(makersComments.getContent());
                comment.setCreateDate(DateUtils.toISOLocalDate(makersComments.getCreatedDateTime()));
                comment.setUpdateDate(DateUtils.toISOLocalDate(makersComments.getUpdatedDateTime()));
                commentList.add(comment);
            } else if (comments instanceof AdminComments adminComments && !adminComments.getIsDelete()) {
                comment.setWriter("admin");
                comment.setContent(adminComments.getContent());
                comment.setCreateDate(DateUtils.toISOLocalDate(adminComments.getCreatedDateTime()));
                comment.setUpdateDate(DateUtils.toISOLocalDate(adminComments.getUpdatedDateTime()));
                commentList.add(comment);
            }
        }
        return commentList;
    }


    @Mapping(source = "reviews.id", target = "reviewId")
    @Mapping(source = "reviews.orderItem", target = "serviceDate", qualifiedByName = "getServiceDate")
    @Mapping(source = "reviews.orderItem.order.code", target = "orderCode")
    @Mapping(source = "reviews.orderItem", target = "itemName", qualifiedByName = "getItemName")
    @Mapping(source = "reviews.orderItem", target = "makersName", qualifiedByName = "getMakersName")
    @Mapping(source = "reviews.satisfaction", target = "satisfaction")
    @Mapping(target = "createdDate", expression = "java(DateUtils.toISOLocalDate(reviews.getCreatedDateTime()))")
    @Mapping(source = "reviews.content", target = "content")
    @Mapping(target = "isReport", expression = "java(reviews.getIsReports() == null || !reviews.getIsReports() ? false : true)")
    @Mapping(source = "reviews.user", target = "writer", qualifiedByName = "getNameAndNickname")
    ReviewAdminResDto.ReviewList toAdminDto(Reviews reviews);

    @Named("getNameAndNickname")
    default String getNameAndNickname(User user) {
        return user.getNameAndNickname();
    }

    default ReviewAdminResDto.ReviewDetail toReviewDetails(Reviews reviews) {
        ReviewAdminResDto.ReviewDetail reviewDetail = new ReviewAdminResDto.ReviewDetail();

        reviewDetail.setReviewId(reviews.getId());
        reviewDetail.setImageLocations(getImagesLocations(reviews.getImages()));
        reviewDetail.setContent(reviews.getContent());
        reviewDetail.setSatisfaction(reviews.getSatisfaction());
        reviewDetail.setContentOrigin(reviews.getContentOrigin());
        reviewDetail.setSatisfactionOrigin(reviews.getSatisfactionOrigin());
        reviewDetail.setForMakers(reviews.getForMakers());
        reviewDetail.setWriter(reviews.getUser().getNickname());
        reviewDetail.setFoodName(reviews.getFood().getName());
        reviewDetail.setIsDelete(reviews.getIsDelete());
        reviewDetail.setIsReport(reviews.getIsReports());
        reviewDetail.setMakersComment(getMakersComment(reviews.getComments()));
        reviewDetail.setAdminComment(getAdminComment(reviews.getComments()));

        return reviewDetail;
    }

    ;

    @Mapping(source = "reqDto.content", target = "content")
    @Mapping(source = "reviews", target = "reviews")
    @Mapping(target = "isDelete", defaultValue = "false")
    AdminComments toAdminComment(CommentReqDto reqDto, Reviews reviews);

    @Mapping(source = "reqDto.content", target = "content")
    @Mapping(source = "reviews", target = "reviews")
    @Mapping(target = "isDelete", defaultValue = "false")
    MakersComments toMakersComment(CommentReqDto reqDto, Reviews reviews);

    default ReviewMakersResDto.ReviewListDto toMakersReviewListDto(Reviews reviews) {
        ReviewMakersResDto.ReviewListDto reviewListDto = new ReviewMakersResDto.ReviewListDto();

        reviewListDto.setReviewId(reviews.getId());
        reviewListDto.setImageLocation(getLocation(reviews.getImages()));
        reviewListDto.setContent(reviews.getContent());
        reviewListDto.setSatisfaction(reviews.getSatisfaction());
        reviewListDto.setContent(reviews.getContent());
        reviewListDto.setUpdateDate(DateUtils.toISOLocalDate(reviews.getUpdatedDateTime()));
        reviewListDto.setCreateDate(DateUtils.toISOLocalDate(reviews.getCreatedDateTime()));
        reviewListDto.setForMakers(reviews.getForMakers());
        reviewListDto.setWriter(reviews.getUser().getNickname());
        reviewListDto.setIsReport(reviews.getIsReports());
        reviewListDto.setOrderItemName(getItemName(reviews.getOrderItem()));
        reviewListDto.setIsMakersComments(false);

        List<Comments> commentsList = reviews.getComments();
        for (Comments comment : commentsList) {
            if (comment instanceof MakersComments) {
                reviewListDto.setIsMakersComments(true);
                break;
            }
        }

        return reviewListDto;
    }

    ;


    default ReviewMakersResDto.ReviewDetail toMakersReviewDetails(Reviews reviews, MultiValueMap<LocalDate, Integer> dateAndScore) {
        ReviewMakersResDto.ReviewDetail reviewDetail = new ReviewMakersResDto.ReviewDetail();

        reviewDetail.setReviewId(reviews.getId());
        reviewDetail.setImageLocation(getImagesLocations(reviews.getImages()));
        reviewDetail.setContent(reviews.getContent());
        reviewDetail.setSatisfaction(reviews.getSatisfaction());
        reviewDetail.setUpdateDate(DateUtils.toISOLocalDate(reviews.getUpdatedDateTime()));
        reviewDetail.setCreateDate(DateUtils.toISOLocalDate(reviews.getCreatedDateTime()));
        reviewDetail.setForMakers(reviews.getForMakers());
        reviewDetail.setWriter(reviews.getUser().getNickname());
        reviewDetail.setItemName(getItemName(reviews.getOrderItem()));
        reviewDetail.setIsReport(reviews.getIsReports());

        List<Comments> commentList = reviews.getComments();
        if (commentList.isEmpty()) reviewDetail.setMakersComment(null);
        else {
            ReviewMakersResDto.MakersComment makersComment = new ReviewMakersResDto.MakersComment();
            for (Comments comments : commentList) {
                if (comments instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                    makersComment.setCommentId(makersComments.getId());
                    makersComment.setContent(makersComments.getContent());
                }
            }
            reviewDetail.setMakersComment(makersComment);
        }
        reviewDetail.setReviewScoreList(getAverageReviewScore(dateAndScore));

        return reviewDetail;
    }

    ;

    default List<ReviewMakersResDto.AverageReviewScore> getAverageReviewScore(MultiValueMap<LocalDate, Integer> dateAndScore) {
        if (dateAndScore == null) return null;
        List<ReviewMakersResDto.AverageReviewScore> averageReviewScoreList = new ArrayList<>();
        for (LocalDate serviceDate : dateAndScore.keySet()) {
            List<Integer> scoreList = dateAndScore.get(serviceDate);

            Integer score = 0;
            for (Integer s : scoreList) {
                score += s;
            }
            Double average = Math.ceil(score / scoreList.size());
            ReviewMakersResDto.AverageReviewScore averageReviewScore = new ReviewMakersResDto.AverageReviewScore();

            averageReviewScore.setDate(DateUtils.localDateToString(serviceDate));
            averageReviewScore.setScore(average);

            averageReviewScoreList.add(averageReviewScore);
        }

        return averageReviewScoreList;
    }

    @Named("getImagesLocations")
    default List<String> getImagesLocations(List<Image> imageList) {
        List<String> nullList = new ArrayList<>();
        if (imageList != null && !imageList.isEmpty()) {
            return imageList.stream().map(Image::getLocation).toList();
        }
        return nullList;
    }

    @Named("getMakersName")
    default String getMakersName(OrderItem orderItem) {
        OrderItem item = (OrderItem) Hibernate.unproxy(orderItem);
        if (item instanceof OrderItemDailyFood orderItemDailyFood) {
            return orderItemDailyFood.getDailyFood().getFood().getMakers().getName();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS);
    }

    @Named("getItemName")
    default String getItemName(OrderItem orderItem) {
        OrderItem item = (OrderItem) Hibernate.unproxy(orderItem);
        if (item instanceof OrderItemDailyFood orderItemDailyFood) {
            return orderItemDailyFood.getDailyFood().getFood().getName();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM);
    }

    @Named("getServiceDate")
    default String getServiceDate(OrderItem orderItem) {
        String serviceDate = null;
        if (Hibernate.unproxy(orderItem) instanceof OrderItemDailyFood orderItemDailyFood) {
            serviceDate = DateUtils.localDateToString(orderItemDailyFood.getDailyFood().getServiceDate());
        }
        return serviceDate;
    }

    @Named("getMakersComment")
    default ReviewAdminResDto.MakersComment getMakersComment(List<Comments> comments) {
        if (comments.isEmpty()) return null;
        ReviewAdminResDto.MakersComment makersComment = new ReviewAdminResDto.MakersComment();
        for (Comments comment : comments) {
            if (comment instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                makersComment.setCommentId(makersComments.getId());
                makersComment.setMakersName(makersComments.getReviews().getFood().getMakers().getName());
                makersComment.setComment(makersComments.getContent());
                makersComment.setIsDelete(makersComments.getIsDelete());
            }
        }
        return makersComment;
    }

    @Named("getAdminComment")
    default ReviewAdminResDto.AdminComment getAdminComment(List<Comments> comments) {
        if (comments.isEmpty()) return null;
        ReviewAdminResDto.AdminComment adminComment = new ReviewAdminResDto.AdminComment();
        for (Comments comment : comments) {
            if (comment instanceof AdminComments adminComments && !adminComments.getIsDelete()) {
                adminComment.setCommentId(adminComments.getId());
                adminComment.setComment(adminComments.getContent());
                adminComment.setIsDelete(adminComments.getIsDelete());
            }
        }
        return adminComment;
    }

    @Named("getLocation")
    default String getLocation(List<Image> imageList) {
        return imageList.isEmpty() ? null : imageList.get(0).getLocation();
    }

    default GetFoodReviewResponseDto toGetFoodReviewResponseDto(List<FoodReviewListDto> foodReviewListDtoList, Double starAverage, Integer totalReview, BigInteger foodId, Integer sort,
                                                                BigInteger reviewWrite, List<String> keywords, Map<Integer, Integer> stars) {
        GetFoodReviewResponseDto getFoodReviewResponseDto = new GetFoodReviewResponseDto();
        getFoodReviewResponseDto.setReviewList(foodReviewListDtoList);
        getFoodReviewResponseDto.setStarAverage(starAverage);
        getFoodReviewResponseDto.setTotalReview(totalReview);
        getFoodReviewResponseDto.setFoodId(foodId);
        getFoodReviewResponseDto.setReviewWrite(reviewWrite);
        getFoodReviewResponseDto.setKeywords(keywords);
        getFoodReviewResponseDto.setStars(stars);



        return getFoodReviewResponseDto;

    }

    ;

}
