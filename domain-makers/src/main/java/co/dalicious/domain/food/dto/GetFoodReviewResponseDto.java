package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "음식의 상세정보에 나오는 리뷰 응답 Dto")
public class GetFoodReviewResponseDto {

    private List<FoodReviewListDto> items;
    private Double starAverage;
    private BigInteger foodId;
    private Integer totalReview;
    private BigInteger reviewWrite;
}
