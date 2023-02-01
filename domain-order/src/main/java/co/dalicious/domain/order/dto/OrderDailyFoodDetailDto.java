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
    private String cardNumber;
    private String cardCompany;
    private List<OrderItem> orderItems;

    @Getter
    @Setter
    public static class OrderItem {
        private BigInteger id;
        private String image;
        private String serviceDate;
        private Integer diningType;
        private String makers;
        private String foodName;
        private Integer count;
        private BigDecimal price;
        private Integer orderStatus;

    }
}
