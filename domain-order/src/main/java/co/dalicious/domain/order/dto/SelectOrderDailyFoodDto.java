package co.dalicious.domain.order.dto;

import co.dalicious.system.enums.DiningType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SelectOrderDailyFoodDto {
    private BigInteger orderItemGroupId;
    private LocalDate serviceDate;
    private DiningType diningType;
    private String groupName;
    private String spotName;
    private String userName;
    private String userNickname;
    private String userEmail;
    private String phone;
    private String orderCode;
    private Timestamp orderDateTime;
    private BigDecimal totalPrice;
    private BigDecimal supportPrice;
    private BigDecimal orderTotalPrice;
    private BigDecimal point;
    private BigDecimal deliveryPrice;
    private Integer isMembership;
    private BigInteger userId;
    private List<SelectOrderItemDailyFoodsDto> selectOrderItemDailyFoodsDtos;
}
