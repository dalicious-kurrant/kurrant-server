package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.order.dto.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

@Schema(description = "홈화면에서 고객이 주문한 음식을 보여주는 DTO")
@Data
public class UserOrderDto {
    Integer id;
    Date serviceDate;
    List<OrderItemDto> orderItem;
}
