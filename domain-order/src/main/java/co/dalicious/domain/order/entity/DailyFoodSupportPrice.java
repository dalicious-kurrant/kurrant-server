package co.dalicious.domain.order.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.converter.MonetaryStatusConverter;
import co.dalicious.domain.order.dto.DailySupportPriceDto;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user__support_price_history")
public class DailyFoodSupportPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("사용자 PK")
    private BigInteger id;

    @ManyToOne(optional = false)
    @JoinColumn
    @Comment("유저")
    private User user;

    @ManyToOne
    @JoinColumn
    @Comment("그룹")
    private Group group;

    @Column(columnDefinition = "Decimal(15,2) DEFAULT '0.00'")
    @Comment("사용 지원금")
    private BigDecimal usingSupportPrice;

    @Comment("서비스일")
    private LocalDate serviceDate;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사 타입(1. 아침, 2. 점심, 3. 저녁)")
    private DiningType diningType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "order_item_daily_food_group_fk")
    @Comment("지원금 사용 아이템")
    private OrderItemDailyFoodGroup orderItemDailyFoodGroup;

    @Convert(converter = MonetaryStatusConverter.class)
    @Comment("지원금 사용 취소 유무 (1. 차감 2. 환불)")
    private MonetaryStatus monetaryStatus;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Builder
    public DailyFoodSupportPrice(User user, Group group, BigDecimal usingSupportPrice, LocalDate serviceDate, DiningType diningType, OrderItemDailyFoodGroup orderItemDailyFoodGroup, MonetaryStatus monetaryStatus) {
        this.user = user;
        this.group = group;
        this.usingSupportPrice = usingSupportPrice;
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.orderItemDailyFoodGroup = orderItemDailyFoodGroup;
        this.monetaryStatus = monetaryStatus;
    }

    public void updateMonetaryStatus(MonetaryStatus monetaryStatus) {
        this.monetaryStatus = monetaryStatus;
    }
    
    // FIXME: 정산에 사용할 주문 상품과 개수 계산 -> "메드트로닉"은 예외
    public List<DailySupportPriceDto> getOrderItemDailyFoodCount() {
        List<DailySupportPriceDto> dailySupportPriceDtos = new ArrayList<>();

        BigDecimal supportPrice = this.usingSupportPrice;

        List<OrderItemDailyFood> orderItemDailyFoods = this.orderItemDailyFoodGroup.getOrderDailyFoods();
        if(orderItemDailyFoods.size() == 1) {
            OrderItemDailyFood orderItemDailyFood = orderItemDailyFoods.get(0);
            // 지원금을 사용한 상품의 개수 추출
            for(int i = 1; i <= orderItemDailyFood.getCount(); i++) {
                BigDecimal discountedPrice = orderItemDailyFood.getDiscountedPrice();
                if(discountedPrice.multiply(BigDecimal.valueOf(i)).compareTo(supportPrice) >= 0) {
                    dailySupportPriceDtos.add(new DailySupportPriceDto(orderItemDailyFood, i, supportPrice));
                }
            }
            return dailySupportPriceDtos;
        }

        orderItemDailyFoods = orderItemDailyFoods.stream().sorted(Comparator.comparing(OrderItemDailyFood::getOrderItemTotalPrice))
                .toList();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if(supportPrice.compareTo(orderItemDailyFood.getOrderItemTotalPrice()) > 0) {
                dailySupportPriceDtos.add(new DailySupportPriceDto(orderItemDailyFood, orderItemDailyFood.getCount(), supportPrice));
            } else if (supportPrice.compareTo(BigDecimal.ZERO) > 0) {
                // 지원금을 사용한 상품의 개수 추출
                for(int i = 1; i <= orderItemDailyFood.getCount(); i++) {
                    BigDecimal discountedPrice = orderItemDailyFood.getDiscountedPrice();
                    if(discountedPrice.multiply(BigDecimal.valueOf(i)).compareTo(supportPrice) >= 0) {
                        dailySupportPriceDtos.add(new DailySupportPriceDto(orderItemDailyFood, i, supportPrice));
                    }
                }
            }
            supportPrice = supportPrice.subtract(orderItemDailyFood.getOrderItemTotalPrice());
        }
        return dailySupportPriceDtos;
    }

    public Integer getCount() {
        BigDecimal supportPrice = this.usingSupportPrice;

        List<OrderItemDailyFood> orderItemDailyFoods = this.orderItemDailyFoodGroup.getOrderDailyFoods();
        // 주문 완료인 상품들만 추출
        orderItemDailyFoods = orderItemDailyFoods.stream()
                .filter(v -> OrderStatus.completePayment().contains(v.getOrderStatus()))
                .sorted(Comparator.comparing(OrderItemDailyFood::getOrderItemTotalPrice))
                .toList();
        if(orderItemDailyFoods.size() == 1) {
            OrderItemDailyFood orderItemDailyFood = orderItemDailyFoods.get(0);
            // 지원금을 사용한 상품의 개수 추출
            for(int i = 1; i <= orderItemDailyFood.getCount(); i++) {
                BigDecimal discountedPrice = orderItemDailyFood.getDiscountedPrice();
                if(discountedPrice.multiply(BigDecimal.valueOf(i)).compareTo(supportPrice) >= 0) {
                    return i;
                }
            }
        }

        int i = 0;
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            if(supportPrice.compareTo(orderItemDailyFood.getOrderItemTotalPrice()) > 0) {
                i += orderItemDailyFood.getCount();
            } else if (supportPrice.compareTo(BigDecimal.ZERO) > 0) {
                // 지원금을 사용한 상품의 개수 추출
                for(int j = 1; j <= orderItemDailyFood.getCount(); j++) {
                    BigDecimal discountedPrice = orderItemDailyFood.getDiscountedPrice();
                    if(discountedPrice.multiply(BigDecimal.valueOf(j)).compareTo(supportPrice) >= 0) {
                        i += j;
                    }
                }
            }
        }
        return i;
    }

    public Order getOrder() {
        return this.orderItemDailyFoodGroup.getOrderDailyFoods().get(0).getOrder();
    }
}
