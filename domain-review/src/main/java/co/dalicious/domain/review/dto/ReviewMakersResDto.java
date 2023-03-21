package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReviewMakersResDto {
    private Integer count;
    private List<ReviewListDto> reviewListDtoList;
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
        private BigInteger foodId;
        private String orderItemName;
    }

    @Getter
    @Setter
    public static class ReviewDetail {
        private BigInteger reviewId;
        private List<String> imageLocation;
        private String content;
        private Integer satisfaction;
        private String createDate;
        private String updateDate;
        private Boolean forMakers;
        private String writer;
        private String itemName;
        private MakersComment makersComment;
        private List<AverageReviewScore> reviewScoreList;
    }

    @Getter
    @Setter
    public static class MakersComment {
        private BigInteger commentId;
        private String content;
    }

    @Getter
    @Setter
    public static class AverageReviewScore {
        private String date;
        private Double score;
    }
}
