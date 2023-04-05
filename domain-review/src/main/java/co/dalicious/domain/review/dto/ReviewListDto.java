package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ReviewListDto {
    private BigInteger reviewId;
    private List<String> imageLocation;
    private String content;
    private Integer satisfaction;
    private String createDate;
    private String updateDate;
    private Boolean forMakers;
    private String makersName;
    private String itemName;
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
    public ReviewListDto(BigInteger reviewId, List<String> imageLocation, String content, Integer satisfaction, String createDate, String updateDate, Boolean forMakers, String makersName, String itemName, List<Comment> commentList) {
        this.reviewId = reviewId;
        this.imageLocation = imageLocation;
        this.content = content;
        this.satisfaction = satisfaction;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.forMakers = forMakers;
        this.makersName = makersName;
        this.itemName = itemName;
        this.commentList = commentList;
    }
}
