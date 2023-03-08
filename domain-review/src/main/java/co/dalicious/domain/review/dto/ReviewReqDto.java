package co.dalicious.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReqDto {
    private Integer satisfaction;
    private String content;
    private Boolean forMakers;
}
