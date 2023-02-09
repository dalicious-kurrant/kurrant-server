package co.dalicious.domain.order.entity;

import co.dalicious.domain.order.converter.OrderStatusConverter;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "order__order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문상세 PK")
    @Column(name="id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "e_order_status")
    @Comment("결제 진행 상태")
    private OrderStatus orderStatus;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "order_fk")
    @Comment("주문 ID")
    private Order order;

    public OrderItem(OrderStatus orderStatus, Order order) {
        this.orderStatus = orderStatus;
        this.order = order;
    }

    public void updateOrderStatus (OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
