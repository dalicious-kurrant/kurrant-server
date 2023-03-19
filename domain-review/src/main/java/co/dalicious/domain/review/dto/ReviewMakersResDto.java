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
        private BigInteger foodId;
        private String orderItemName;
        private Integer count;

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
    }

    @Getter
    @Setter
    public static class MakersComment {
        private BigInteger commentId;
        private String content;
    }
}
