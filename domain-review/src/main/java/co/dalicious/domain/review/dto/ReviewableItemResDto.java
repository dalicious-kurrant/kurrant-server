package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ReviewableItemResDto {
    private Integer count;
    private List<ReviewableItemListDto> items;

    public static ReviewableItemResDto create(List<ReviewableItemListDto> items) {
        return ReviewableItemResDto.builder()
                .count(items.size())
                .items(items)
                .build();
    }
}
