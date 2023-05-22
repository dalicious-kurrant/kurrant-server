package co.kurrant.app.public_api.dto.food;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "음식 상세 리뷰에 좋아요 저장 요청 Dto")
public class FoodReviewLikeDto {
    private BigInteger dailyFoodId;
    private BigInteger reviewId;
}
