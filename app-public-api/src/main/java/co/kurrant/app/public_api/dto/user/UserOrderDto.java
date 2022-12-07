package co.kurrant.app.public_api.dto.user;

import co.dalicious.domain.user.dto.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Schema(description = "홈화면에서 고객이 주문한 음식을 보여주는 DTO")
@Data
public class UserOrderDto {
    Integer id;
    Date serviceDate;
    List<OrderItemDto> orderItem;
}
