package co.dalicious.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@Setter
public class SelectCommentByReviewDto {
    private BigInteger commentId;
    private String writer;
    private String content;
    private Timestamp createDate;
    private Timestamp updateDate;
}
