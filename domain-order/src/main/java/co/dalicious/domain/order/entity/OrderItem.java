package co.dalicious.domain.order.entity;

import co.dalicious.domain.order.converter.OrderStatusConverter;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

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
    @NotNull
    @Column(name="id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "e_order_status")
    @Comment("결제 진행 상태")
    private OrderStatus orderStatus;

    @CreationTimestamp
    @Column(name = "created", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private LocalDate updated;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "order_fk")
    @Comment("주문 ID")
    private Order order;

    public OrderItem(OrderStatus orderStatus, Order order) {
        this.orderStatus = orderStatus;
        this.order = order;
    }
}
