package co.dalicious.domain.order;

import co.dalicious.domain.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order__order")
public class Order {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "total_price", nullable = false, precision = 15)
    private BigDecimal totalPrice;

    @Size(max = 8)
    @NotNull
    @Column(name = "e_payment_type", nullable = false, length = 8)
    private String ePaymentType;

    @NotNull
    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}