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
    private MakersComment makersComment;
    private AdminComment adminComment;

    @Getter
    @Setter
    public static class MakersComment{
        private String content;
        private String createDate;
        private String updateDate;
    }

    @Getter
    @Setter
    public static class AdminComment{
        private String content;
        private String createDate;
        private String updateDate;
    }

    @Builder
    public ReviewListDto(BigInteger reviewId, List<String> imageLocation, String content, Integer satisfaction, String createDate, String updateDate, Boolean forMakers, String makersName, String itemName, MakersComment makersComment, AdminComment adminComment) {
        this.reviewId = reviewId;
        this.imageLocation = imageLocation;
        this.content = content;
        this.satisfaction = satisfaction;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.forMakers = forMakers;
        this.makersName = makersName;
        this.itemName = itemName;
        this.makersComment = makersComment;
        this.adminComment = adminComment;
    }
}
