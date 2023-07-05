package co.dalicious.domain.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class OrderDailyFoodDetailDto {
    private String code;
    private String orderDate;
    private String orderType;
    private String userName;
    private String groupName;
    private String spotName;
    private String ho;
    private String phone;
    private String memo;
    private String address;
    private BigDecimal defaultPrice;
    private BigDecimal supportPrice;
    private BigDecimal membershipDiscountPrice;
    private BigDecimal makersDiscountPrice;
    private BigDecimal periodDiscountPrice;
    private BigDecimal deliveryFee;
    private BigDecimal point;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private String receiptUrl;
    private String paymentCompany;
    private RefundDto refundDto;
    private List<OrderItem> orderItems;

    @Getter
    @Setter
    public static class OrderItem {
        private BigInteger id;
        private String deliveryTime;
        private String image;
        private String serviceDate;
        private Integer diningType;
        private String makers;
        private String foodName;
        private Integer count;
        private BigDecimal price;
        private Integer orderStatus;
        private Integer dailyFoodStatus;
        private Boolean isBeforeLastOrderTime;

    }

    @Getter
    @Setter
    public static class RefundDto {
        private BigDecimal refundPayPrice;
        private BigDecimal refundItemPrice;
        private BigDecimal refundSupportPrice;
        private BigDecimal refundDeliveryFee;
        private BigDecimal refundDeduction;
        private BigDecimal refundTotalPrice;
        private BigDecimal refundCardPrice;
        private BigDecimal refundTotalPoint;

        public RefundDto(BigDecimal refundPayPrice, BigDecimal refundItemPrice, BigDecimal refundSupportPrice, BigDecimal refundDeliveryFee, BigDecimal refundDeduction, BigDecimal refundTotalPrice, BigDecimal refundCardPrice, BigDecimal refundTotalPoint) {
            this.refundPayPrice = refundPayPrice;
            this.refundItemPrice = refundItemPrice;
            this.refundSupportPrice = refundSupportPrice;
            this.refundDeliveryFee = refundDeliveryFee;
            this.refundDeduction = refundDeduction;
            this.refundTotalPrice = refundTotalPrice;
            this.refundCardPrice = refundCardPrice;
            this.refundTotalPoint = refundTotalPoint;
        }
    }
}
