package co.dalicious.domain.food.dto;

import co.dalicious.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private Boolean isWriter;
    private Integer good;
    @Schema(description = "내가 좋아요를 눌렀는지 여부")
    private Boolean isGood;
    private String content;
    private Integer satisfaction;
    private String createDate;
    private String updateDate;
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
                             String updateDate, List<Comment> commentList, String userName, Integer good, Boolean isGood,
                             Boolean isWriter) {
        this.reviewId = reviewId;
        this.imageLocation = imageLocation;
        this.content = content;
        this.satisfaction = satisfaction;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.commentList = commentList;
        this.userName = userName;
        this.good = good;
        this.isGood = isGood;
        this.isWriter = isWriter;
    }
}
