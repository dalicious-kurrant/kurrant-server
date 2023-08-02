package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Schema(description = "음식의 상세정보에 나오는 리뷰 응답 Dto")
public class GetFoodReviewResponseDto {

    private List<FoodReviewListDto> reviewList;
    private List<String> keywords;
    private Map<Integer, Integer> stars;
    private Double starAverage;
    private BigInteger foodId;
    private Integer totalReview;
    private BigInteger reviewWrite;
}
