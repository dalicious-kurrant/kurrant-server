package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ReviewMakersResDto {

    @Getter
    @Setter
    public static class ReviewListDto {
        private BigInteger reviewId;
        private String imageLocation;
        private String content;
        private Integer satisfaction;
        private String createDate;
        private String updateDate;
        private Boolean forMakers;
        private String writer;
        private String itemName;
        private Integer count;

        @Builder
        public ReviewListDto(BigInteger reviewId, String imageLocation, String content, Integer satisfaction, String createDate, String updateDate, Boolean forMakers, String writer, String itemName, Integer count) {
            this.reviewId = reviewId;
            this.imageLocation = imageLocation;
            this.content = content;
            this.satisfaction = satisfaction;
            this.createDate = createDate;
            this.updateDate = updateDate;
            this.forMakers = forMakers;
            this.writer = writer;
            this.itemName = itemName;
            this.count = count;
        }
    }

    @Getter
    @Setter
    public static class ReviewDetail {
        private BigInteger reviewId;
        private String imageLocation;
        private String content;
        private Integer satisfaction;
        private String createDate;
        private String updateDate;
        private Boolean forMakers;
        private String writer;
        private String itemName;
        private MakersComment makersComment;
    }

    @Getter
    @Setter
    public static class MakersComment {
        private BigInteger commentId;
        private String content;
    }
}
