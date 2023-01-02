package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Schema(description = "주문 상세 Dto")
@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDto {
    Integer id;
    String serviceDate;
    List<OrderItemDto> orderItemDtoList;

}
