package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "주문 내역 조회 DTO")
public class OrderHistoryDto {
    private BigInteger id;
    private Integer orderType;
    private String code;
    private String orderDate;
    private List<OrderItem> orderItems;


    @Getter
    @Setter
    public static class OrderItem {
        private BigInteger id;
        private String deliveryTime;
        private String lastOrderTime;
        private String makersName;
        private String name;
        private String image;
        private String serviceDate;
        private Integer diningType;
        private Integer count;
        private BigDecimal price;
        private Integer dailyFoodStatus;
        private Integer orderStatus;
    }
}
