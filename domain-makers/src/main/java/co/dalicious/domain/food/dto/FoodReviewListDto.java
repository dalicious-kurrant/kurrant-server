package co.dalicious.domain.food.dto;

import co.dalicious.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class FoodReviewListDto {
    private BigInteger reviewId;
    private List<String> imageLocation;
    private String userName;
    private Integer like;
    private String content;
    private Integer satisfaction;
    private String createDate;
    private List<Comment> commentList;

    @Getter
    @Setter
    public static class Comment{
        private String writer;
        private String content;
        private String createDate;
        private String updateDate;
    }

    @Builder
    public FoodReviewListDto(BigInteger reviewId, List<String> imageLocation, String content, Integer satisfaction, String createDate,
                             List<Comment> commentList, String userName, Integer like) {
        this.reviewId = reviewId;
        this.imageLocation = imageLocation;
        this.content = content;
        this.satisfaction = satisfaction;
        this.createDate = createDate;
        this.commentList = commentList;
        this.userName = userName;
        this.like = like;
    }
}
