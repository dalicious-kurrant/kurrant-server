package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "해당 날짜에 주문한 음식들")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private String name;
    private String diningType;
    private String img;
    private Integer count;
}
