package co.dalicious.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Getter
@Setter
public class ReviewReqDto {
    @NotNull
    private BigInteger orderItemId;
    private Integer satisfaction;
    private String content;
    private Boolean forMakers;
}
