package co.dalicious.domain.review.dto;

import co.dalicious.system.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ReviewableItemResDto {
    private Integer count;
    private BigDecimal redeemablePoints;
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

    public static ReviewableItemResDto create(List<OrderFood> orderFoodList, BigDecimal redeemablePoints) {
        return ReviewableItemResDto.builder()
                .count(orderFoodList.size())
                .redeemablePoints(redeemablePoints)
                .orderFoodList(orderFoodList)
                .build();
    }
}
