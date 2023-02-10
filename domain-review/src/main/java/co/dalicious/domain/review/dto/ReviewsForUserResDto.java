package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ReviewsForUserResDto {
    private Integer count;
    private List<ReviewListDto> items;

    public static ReviewsForUserResDto create(List<ReviewListDto> reviews) {
        return ReviewsForUserResDto.builder()
                .count(reviews.size())
                .items(reviews)
                .build();
    }
}
