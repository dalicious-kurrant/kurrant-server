package co.dalicious.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Getter
@Setter
public class ReviewReqDto {

    @NotNull(message = "주문 상품의 id 값은 필수 입력값입니다.")
    private BigInteger orderItemId;

    @NotNull(message = "만족도를 입력해주세요.")
    private Integer satisfaction;

    @NotNull(message = "리뷰 내용을 작성해주세요.")
    private String content;

    private Boolean forMakers;
}
