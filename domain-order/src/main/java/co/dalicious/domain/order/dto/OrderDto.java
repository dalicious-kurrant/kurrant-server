package co.dalicious.domain.order.dto;

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
    @Schema(description = "백오피스 주문 상품 리스트 조회 DTO")
    public static class OrderItemDailyFoodList {
        private BigInteger spotId;
        private Integer spotFoodCount;
        private String spotName;
        private String diningType;
        private List<SpotFoodMap> foodMap;
        private List<OrderItemDailyFoodGroupList> orderItemDailyFoodGroupList;

    }
    @Getter
    @Setter
    public static class SpotFoodMap {
        private String foodName;
        private Integer count;
    }

    @Getter
    @Setter
    public static class ClientOrderItemDailyFood {
        private BigInteger orderItemDailyFoodId;
        private String serviceDate;
        private String groupName;
        private String spotName;
        private String userName;
        private String userEmail;
        private String phone;
        private String diningType;
        private String deliveryTime;
        private String makers;
        private String foodName;
        private Integer count;
        private BigDecimal price;
        private String orderCode;
        private String orderStatus;
        private String orderDateTime;
    }

    @Getter
    @Setter
    public static class OrderItemDailyFoodGroupList {
        private String serviceDate;
        private String diningType;
        private String groupName;
        private String spotName;
        private String userName;
        private String userEmail;
        private String phone;
        private String orderCode;
        private String orderDateTime;
        private BigDecimal totalPrice;
        private BigDecimal supportPrice;
        private BigDecimal payPrice;
        private BigDecimal deliveryPrice;
        private List<OrderItemDailyFood> orderItemDailyFoods;
    }

    @Getter
    @Setter
    public static class OrderItemDailyFood {
        private BigInteger orderItemDailyFoodId;
        private String deliveryTime;
        private String makers;
        private String foodName;
        private Integer count;
        private BigDecimal price;
        private BigDecimal supplyPrice;
        private String orderStatus;
    }

    @Getter
    @Setter
    @Schema(description = "고객사 주문 상품 리스트 조회 DTO")
    public static class GroupOrderItemDailyFoodList {
        private BigDecimal totalPrice;
        private Integer totalFoodCount;
        private Integer buyingUserCount;
        private List<ClientOrderItemDailyFood> orderItemDailyFoods;

    }

    @Getter
    @Setter
    @Schema(description = "백오피스 주문 상세 조회 DTO")
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
    @Schema(description = "고객사 주간 주문 현황")
    public static class OrderItemStatic {
        private String serviceDate;
        private String diningType;
        private Integer userCount;
        private Integer orderUserCount;
        private Integer buyingUserCount;
        private Integer foodCount;
        private BigDecimal orderRate;
        private BigDecimal cancelRate;
        private BigDecimal totalPrice;
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

    @Getter
    @Setter
    public static class IdList {
        private List<BigInteger> idList;
    }

    @Getter
    @Setter
    public static class StatusAndIdList {
        private Long status;
        private List<BigInteger> idList;
    }

    @Getter
    @Setter
    public static class Id {
        private BigInteger id;
    }
}
