package co.dalicious.domain.review.dto;

import co.dalicious.domain.file.entity.embeddable.Image;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class SelectAppReviewByUserDto {
    private BigInteger reviewId;
    private BigInteger dailyFoodId;
    private String content;
    private Integer satisfaction;
    private Timestamp createDate;
    private Timestamp updateDate;
    private Boolean forMakers;
    private String makersName;
    private String itemName;
    private List<Image> images;
    private List<SelectCommentByReviewDto> commentList;
}
