package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;

@Schema(description = "해당 날짜에 주문한 음식들")
@Getter
@Setter
@AllArgsConstructor
public class OrderItemDto {
    private BigInteger id;
    private String name;
    private Integer orderStatus;
    private String makers;
    private String image;
    private Integer count;
}
