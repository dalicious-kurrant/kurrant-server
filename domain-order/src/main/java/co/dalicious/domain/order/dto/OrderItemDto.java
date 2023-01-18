package co.dalicious.domain.order.dto;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "해당 날짜에 주문한 음식들")
@Getter
@Setter
@AllArgsConstructor
public class OrderItemDto {
    private String name;
    private String diningType;
    private String img;
    private Integer count;
}
