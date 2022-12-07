package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Schema(description = "주문 상세 Dto")
@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDto {
    Integer id;
    Date serviceDate;
    List<OrderItemDto> orderItemDtoList;

}
