package co.dalicious.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ReviewUpdateReqDto {

    @NotNull(message = "만족도를 입력해주세요.")
    private Integer satisfaction;

    @NotNull(message = "리뷰 내용을 작성해주세요.")
    private String content;

    private List<String> images;
}
