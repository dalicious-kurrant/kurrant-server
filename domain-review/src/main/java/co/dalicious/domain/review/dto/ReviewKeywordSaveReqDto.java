package co.dalicious.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "리뷰 키워드를 추가하는 요청 DTO")
public class ReviewKeywordSaveReqDto {

    private BigInteger foodId;
    private List<String> names;

}

