package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Schema(description = "주문 상세 Dto")
@Getter
@Setter
public class OrderDetailDto {
    BigInteger id;
    String serviceDate;
    List<OrderItemDto> orderItemDtoList;

}
