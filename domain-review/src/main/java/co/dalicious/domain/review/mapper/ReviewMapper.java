package co.dalicious.domain.review.mapper;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.review.dto.*;
import co.dalicious.domain.review.entity.AdminComments;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.MultiValueMap;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DateUtils.class, Math.class})
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

    @Named("setCommentList")
    default List<ReviewListDto.Comment> setCommentList(List<Comments> commentsList) {
        List<ReviewListDto.Comment> commentList = new ArrayList<>();

        if(commentsList.isEmpty()) return commentList;

        commentsList = commentsList.stream().sorted(Comparator.comparing(Comments::getCreatedDateTime)).toList();

        for(Comments comments : commentsList) {
            ReviewListDto.Comment comment = new ReviewListDto.Comment();
            if(comments instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                comment.setWriter(makersComments.getReviews().getFood().getMakers().getName());
                comment.setContent(makersComments.getContent());
                comment.setCreateDate(DateUtils.toISOLocalDate(makersComments.getCreatedDateTime()));
                comment.setUpdateDate(DateUtils.toISOLocalDate(makersComments.getUpdatedDateTime()));
                commentList.add(comment);
            } else if(comments instanceof AdminComments adminComments && !adminComments.getIsDelete()) {
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
    @Mapping(source = "reviews.user.name", target = "writer")
    ReviewAdminResDto.ReviewList toAdminDto(Reviews reviews);

    default ReviewAdminResDto.ReviewDetail toReviewDetails(Reviews reviews) {
        ReviewAdminResDto.ReviewDetail reviewDetail = new ReviewAdminResDto.ReviewDetail();

        reviewDetail.setReviewId(reviews.getId());
        reviewDetail.setImageLocations(getImagesLocations(reviews.getImages()));
        reviewDetail.setContent(reviews.getContent());
        reviewDetail.setSatisfaction(reviews.getSatisfaction());
        reviewDetail.setContentOrigin(reviews.getContentOrigin());
        reviewDetail.setSatisfactionOrigin(reviews.getSatisfactionOrigin());
        reviewDetail.setForMakers(reviews.getForMakers());
        reviewDetail.setWriter(reviews.getUser().getName());
        reviewDetail.setFoodName(reviews.getFood().getName());
        reviewDetail.setMakersComment(getMakersComment(reviews.getComments()));
        reviewDetail.setAdminComment(getAdminComment(reviews.getComments()));

        return reviewDetail;
    };

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
        reviewListDto.setWriter(reviews.getUser().getName());
        reviewListDto.setIsReport(reviews.getIsReports());
        reviewListDto.setOrderItemName(getItemName(reviews.getOrderItem()));

        return  reviewListDto;
    };


    default ReviewMakersResDto.ReviewDetail toMakersReviewDetails(Reviews reviews, MultiValueMap<LocalDate, Integer> dateAndScore) {
        ReviewMakersResDto.ReviewDetail reviewDetail = new ReviewMakersResDto.ReviewDetail();

        reviewDetail.setReviewId(reviews.getId());
        reviewDetail.setImageLocation(getImagesLocations(reviews.getImages()));
        reviewDetail.setContent(reviews.getContent());
        reviewDetail.setSatisfaction(reviews.getSatisfaction());
        reviewDetail.setUpdateDate(DateUtils.toISOLocalDate(reviews.getUpdatedDateTime()));
        reviewDetail.setCreateDate(DateUtils.toISOLocalDate(reviews.getCreatedDateTime()));
        reviewDetail.setForMakers(reviews.getForMakers());
        reviewDetail.setWriter(reviews.getUser().getName());
        reviewDetail.setItemName(getItemName(reviews.getOrderItem()));
        reviewDetail.setIsReport(reviews.getIsReports());

        List<Comments> commentList = reviews.getComments();
        if(commentList.isEmpty()) reviewDetail.setMakersComment(null);
        else {
            ReviewMakersResDto.MakersComment makersComment = new ReviewMakersResDto.MakersComment();
            for(Comments comments : commentList) {
                if(comments instanceof MakersComments makersComments && !makersComments.getIsDelete()) {
                    makersComment.setCommentId(makersComments.getId());
                    makersComment.setContent(makersComments.getContent());
                }
            }
            reviewDetail.setMakersComment(makersComment);
        }
        reviewDetail.setReviewScoreList(getAverageReviewScore(dateAndScore));

        return reviewDetail;
    };

    default List<ReviewMakersResDto.AverageReviewScore> getAverageReviewScore(MultiValueMap<LocalDate, Integer> dateAndScore) {
        if(dateAndScore == null) return null;
        List<ReviewMakersResDto.AverageReviewScore> averageReviewScoreList = new ArrayList<>();
        for(LocalDate serviceDate : dateAndScore.keySet()) {
            List<Integer> scoreList = dateAndScore.get(serviceDate);

            Integer score = 0;
            for(Integer s : scoreList) {
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
    default List<String>  getImagesLocations(List<Image> imageList) {
        if(imageList != null && !imageList.isEmpty()) {
            return imageList.stream().map(Image::getLocation).toList();
        }
        return null;
    }

    @Named("getMakersName")
    default String getMakersName(OrderItem orderItem) {
        OrderItem item = (OrderItem) Hibernate.unproxy(orderItem);
        if(item instanceof OrderItemDailyFood orderItemDailyFood) {
            return orderItemDailyFood.getDailyFood().getFood().getMakers().getName();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS);
    }

    @Named("getItemName")
    default String getItemName(OrderItem orderItem) {
        OrderItem item = (OrderItem) Hibernate.unproxy(orderItem);
        if(item instanceof OrderItemDailyFood orderItemDailyFood) {
            return orderItemDailyFood.getDailyFood().getFood().getName();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM);
    }

    @Named("getServiceDate")
    default String getServiceDate(OrderItem orderItem) {
        String serviceDate = null;
        if(Hibernate.unproxy(orderItem) instanceof OrderItemDailyFood orderItemDailyFood) {
            serviceDate = DateUtils.localDateToString(orderItemDailyFood.getDailyFood().getServiceDate());
        }
        return serviceDate;
    }

    @Named("getMakersComment")
    default List<ReviewAdminResDto.MakersComment> getMakersComment(List<Comments> comments) {
        if(comments.isEmpty()) return null;
        List<ReviewAdminResDto.MakersComment> makersCommentList = new ArrayList<>();
        for(Comments comment : comments) {
            ReviewAdminResDto.MakersComment makersComment = new ReviewAdminResDto.MakersComment();
            if(comment instanceof MakersComments makersComments) {
                makersComment.setCommentId(makersComments.getId());
                makersComment.setMakersName(makersComments.getReviews().getFood().getMakers().getName());
                makersComment.setComment(makersComments.getContent());
                makersComment.setIsDelete(makersComments.getIsDelete());
                makersCommentList.add(makersComment);
            }
        }
        return makersCommentList;
    }

    @Named("getAdminComment")
    default List<ReviewAdminResDto.AdminComment> getAdminComment(List<Comments> comments) {
        if(comments.isEmpty()) return null;
        List<ReviewAdminResDto.AdminComment> adminCommentList = new ArrayList<>();
        for(Comments comment : comments) {
            ReviewAdminResDto.AdminComment adminComment = new ReviewAdminResDto.AdminComment();
            if(comment instanceof AdminComments adminComments) {
                adminComment.setCommentId(adminComments.getId());
                adminComment.setComment(adminComments.getContent());
                adminComment.setIsDelete(adminComments.getIsDelete());
                adminCommentList.add(adminComment);
            }
        }
        return adminCommentList;
    }

    @Named("getLocation")
    default String getLocation(List<Image> imageList) {
        return imageList.isEmpty() ? null : imageList.get(0).getLocation();
    }

}
