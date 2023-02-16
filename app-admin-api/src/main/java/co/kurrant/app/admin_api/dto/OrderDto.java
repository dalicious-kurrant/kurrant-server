package co.kurrant.app.admin_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "주문 상품 DTO")
public class OrderDto {
    @Getter
    @Setter
    @Schema(description = "주문 상품 리스트 조회 DTO")
    public static class OrderItemDailyFoodList {
        private BigInteger spotId;
        private Integer spotFoodCount;
        private String spotName;
        private String diningType;
        private List<SpotFoodMap> foodMap;
        private List<OrderItemDailyFood> orderItemDailyFoods;

    }
    @Getter
    @Setter
    public static class SpotFoodMap {
        private String foodName;
        private Integer count;
    }
    @Getter
    @Setter
    public static class OrderItemDailyFood {
        private BigInteger orderItemDailyFoodId;
        private String serviceDate;
        private String makers;
        private String foodName;
        private BigDecimal price;
        private Integer count;
        private String userName;
        private String phone;
        private String orderCode;

    }

    @Getter
    @Setter
    @Schema(description = "주문 상세 조회 DTO")
    public static class OrderDailyFoodDetail {
        private String orderCode;
        private String userName;
        private String servicePeriod;
        private String spotName;
        private BigDecimal totalPrice;
        private BigDecimal usingSupportPrice;
        private BigDecimal deliveryFee;
    }

    @Getter
    @Setter
    public static class OrderItemDailyFoodDetail {
        private String serviceDate;
        private String makers;
        private String foodName;
        private BigDecimal discountedPrice;
        private Integer count;
        private String orderStatus;
    }
}
