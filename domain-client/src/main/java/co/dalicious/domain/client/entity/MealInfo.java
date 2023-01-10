package co.dalicious.domain.client.entity;

import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Table(name = "client__meal_info")
public class MealInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    @Comment("식사 타입")
    private DiningType diningType;

    @NotNull
    @Column(name = "delivery_time", nullable = false)
    @Comment("배송 시간")
    private LocalTime deliveryTime;

    @NotNull
    @Column(name = "last_order_time", nullable = false)
    @Comment("주문 마감 시간")
    private LocalTime lastOrderTime;

    @Size(max = 255)
    @Column(name = "emb_use_days")
    private String serviceDays;


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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonManagedReference(value = "client__apartment_fk")
    @JoinColumn
    @Comment("그룹")
    private Group group;

    public MealInfo(DiningType diningType, LocalTime deliveryTime, LocalTime lastOrderTime, String serviceDays) {
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.lastOrderTime = lastOrderTime;
        this.serviceDays = serviceDays;
    }
}
