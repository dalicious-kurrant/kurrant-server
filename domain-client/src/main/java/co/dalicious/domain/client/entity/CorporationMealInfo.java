package co.dalicious.domain.client.entity;

import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "client__corporation_meal_info")
public class CorporationMealInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    @Comment("식사 타입")
    private DiningType diningType;

    @NotNull
    @Column(name = "daily_support_price", nullable = false, precision = 15)
    @Comment("일일 회사 지원금")
    private BigDecimal supportPrice;

    @NotNull
    @Column(name = "delivery_time", nullable = false)
    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @NotNull
    @Column(name = "last_order_time", nullable = false)
    @Comment("주문 마감 시간")
    private LocalTime lastOrderTime;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference(value = "client__corporation_fk")
    @JoinColumn
    @Comment("기업")
    private Corporation corporation;
}
