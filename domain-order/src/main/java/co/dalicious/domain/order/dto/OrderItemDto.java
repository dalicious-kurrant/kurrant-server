package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "해당 날짜에 주문한 음식들")
@Setter
@Getter
public class OrderItemDto {
    private String name;
    private String diningType;
    private String img;
    private Integer count;

    @Builder
    public OrderItemDto(String name, String diningType, String img, Integer count) {
        this.name = name;
        this.diningType = diningType;
        this.img = img;
        this.count = count;
    }
}
