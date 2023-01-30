package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "주문 상세 Dto")
@Getter
@Setter
public class OrderDetailDto {
    private String serviceDate;
    private String diningType;
    private List<OrderItemDto> orderItemDtoList;

    @Builder
    public OrderDetailDto(String serviceDate, String diningType, List<OrderItemDto> orderItemDtoList) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.orderItemDtoList = orderItemDtoList;
    }
}
