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
        int size = 0;
        if(!items.isEmpty()) size = items.size();

        return ReviewableItemResDto.builder()
                .count(size)
                .items(items)
                .build();
    }
}
