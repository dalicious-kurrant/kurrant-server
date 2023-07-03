package co.dalicious.domain.review.dto;

import co.dalicious.domain.food.entity.Makers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ReviewAdminResDto {

    private Long unansweredCount;
    private List<MakersInfo> makersInfoList;
    private List<ReviewList> reviewList;

    @Getter
    @Setter
    @Builder
    public static class MakersInfo {
        private BigInteger makersId;
        private String makersName;

        public static MakersInfo create(Makers makers) {
            return MakersInfo.builder()
                    .makersId(makers.getId())
                    .makersName(makers.getName())
                    .build();
        }

    }

    @Getter
    @Setter
    public static class ReviewList {
        private BigInteger reviewId;
        private String serviceDate;
        private String orderCode;
        private String itemName;
        private String makersName;
        private Integer satisfaction;
        private String writer;
        private String createdDate;
        private String content;
        private Boolean forMakers;
        private Boolean isMakersComment;
        private Boolean isAdminComment;
        private Boolean isReport;
        private Boolean isDelete;

        @Builder
        public ReviewList(BigInteger reviewId, String serviceDate, String orderCode, String itemName, String makersName,
                          Integer satisfaction, String writer, String createdDate, String content, Boolean isMakersComment,
                          Boolean isAdminComment, Boolean isReport, Boolean isDelete, Boolean forMakers) {
            this.reviewId = reviewId;
            this.serviceDate = serviceDate;
            this.orderCode = orderCode;
            this.itemName = itemName;
            this.makersName = makersName;
            this.satisfaction = satisfaction;
            this.writer = writer;
            this.createdDate = createdDate;
            this.content = content;
            this.isMakersComment = isMakersComment;
            this.isAdminComment = isAdminComment;
            this.isReport = isReport;
            this.isDelete = isDelete;
            this.forMakers = forMakers;
        }
    }

    public static ReviewAdminResDto create(List<Makers> makersList, List<ReviewList> reviewList, Long count) {
        List<MakersInfo> makersInfos = makersList.stream().map(MakersInfo::create).collect(Collectors.toList());
        return ReviewAdminResDto.builder()
                .unansweredCount(count)
                .makersInfoList(makersInfos)
                .reviewList(reviewList)
                .build();
    }

    @Getter
    @Setter
    public static class ReviewDetail {
        private BigInteger reviewId;
        private List<String> imageLocations;
        private String content;
        private Integer satisfaction;
        private String contentOrigin;
        private Integer satisfactionOrigin;
        private Boolean forMakers;
        private String writer;
        private String foodName;
        private Boolean isDelete;
        private Boolean isReport;
        private MakersComment makersComment;
        private AdminComment adminComment;
    }

    @Getter
    @Setter
    public static class MakersComment {
        private BigInteger commentId;
        private String comment;
        private String makersName;
        private Boolean isDelete;
    }

    @Getter
    @Setter
    public static class AdminComment {
        private BigInteger commentId;
        private String comment;
        private Boolean isDelete;
    }
}
