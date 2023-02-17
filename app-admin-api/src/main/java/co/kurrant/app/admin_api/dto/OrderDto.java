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
        private String groupName;
        private String spotName;
        private String userName;
        private String phone;
        private String diningType;
        private String deliveryTime;
        private String makers;
        private String foodName;
        private Integer count;
        private BigDecimal price;
        private String orderCode;
    }

    @Getter
    @Setter
    @Schema(description = "주문 상세 조회 DTO")
    public static class OrderDailyFoodDetail {
        private BigInteger orderId;
        private String orderCode;
        private String userName;
        private String servicePeriod;
        private String spotName;
        private BigDecimal totalPrice;
        private BigDecimal usingSupportPrice;
        private BigDecimal deliveryFee;
        private BigDecimal point;
        private List<OrderItemDailyFoodGroup> orderItemDailyFoodGroups;
    }

    @Getter
    @Setter
    public static class OrderItemDailyFoodGroup {
        private String serviceDate;
        private String diningType;
        private BigDecimal totalPrice;
        private BigDecimal supportPrice;
        private BigDecimal payPrice;
        private BigDecimal deliveryPrice;
        private List<OrderItemDailyFoodGroupItem> orderItemDailyFoods;
    }

    @Getter
    @Setter
    public static class OrderItemDailyFoodGroupItem {
        private BigInteger orderItemDailyFoodId;
        private String makers;
        private String foodName;
        private BigDecimal discountedPrice;
        private Integer count;
        private String orderStatus;
    }
}
