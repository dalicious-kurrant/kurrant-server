package co.dalicious.domain.review.dto;

import co.dalicious.system.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ReviewableItemResDto {
    private Integer count;
    private List<OrderFood> orderFoodList;

    @Getter
    @Setter
    @Builder
    public static class OrderFood {
        private String serviceDate;
        private List<ReviewableItemListDto> items;

        public static OrderFood create(List<ReviewableItemListDto> items, LocalDate serviceDate) {
            return OrderFood.builder()
                    .serviceDate(serviceDate != null ? DateUtils.localDateToString(serviceDate) : null)
                    .items(items)
                    .build();
        }
    }

    public static ReviewableItemResDto create(List<OrderFood> orderFoodList) {
        return ReviewableItemResDto.builder()
                .count(orderFoodList.size())
                .orderFoodList(orderFoodList)
                .build();
    }
}
