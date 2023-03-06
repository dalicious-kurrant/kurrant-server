package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.converter.DayAndTimeConverter;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Table(name = "client__meal_info", uniqueConstraints={@UniqueConstraint(columnNames={"e_dining_type", "spot_id"})})
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

    @Column(name = "membership_benefit_time")
    @Convert(converter = DayAndTimeConverter.class)
    @Comment("멤버십 혜택 마감 일시")
    private DayAndTime membershipBenefitTime;

    @NotNull
    @Column(name = "last_order_time", nullable = false)
    @Comment("주문 마감 시간")
    private LocalTime lastOrderTime;

    @Size(max = 255)
    @Column(name = "emb_use_days")
    @Comment("서비스 이용 요일")
    private String serviceDays;


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
    @JsonManagedReference(value = "client__spot_fk")
    @JoinColumn
    @Comment("스팟")
    private Spot spot;

    public MealInfo(DiningType diningType, LocalTime deliveryTime, DayAndTime membershipBenefitTime,LocalTime lastOrderTime, String serviceDays, Spot spot) {
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
        this.membershipBenefitTime = membershipBenefitTime;
        this.lastOrderTime = lastOrderTime;
        this.serviceDays = serviceDays;
        this.spot = spot;
    }

    public void updateMealInfo(String serviceDays) {
        this.serviceDays = serviceDays;
    }

    public void updateMealInfo(MealInfo mealInfo) {
        this.deliveryTime = mealInfo.getDeliveryTime();
        this.membershipBenefitTime = mealInfo.getMembershipBenefitTime();
        this.lastOrderTime = mealInfo.getLastOrderTime();
        this.serviceDays = mealInfo.getServiceDays();
    }

    public String dayAndTimeToString() {
        if(this.getMembershipBenefitTime() == null) {
            return "정보 없음";
        }
        else {
            String timeStr = this.getMembershipBenefitTime().getTime().toString();
            if (timeStr.matches("\\d{2}:\\d{2}")) {
                return "0일전 " + timeStr;
            } else {
                return this.getMembershipBenefitTime().getDay() + "일전 " + timeStr;
            }
        }
    }

    public static DayAndTime stringToDayAndTime(String str) {
        if (str.equals("정보 없음")) {
            return null;
        }
       else {
            String[] parts = str.split("일전 ");
            Integer day = 0;
            LocalTime time;
            if (parts.length == 1) {
                time = LocalTime.parse(parts[0]);
            } else {
                day = Integer.parseInt(parts[0]);
                time = LocalTime.parse(parts[1]);
            }
            return new DayAndTime(day, time);
        }
    }
}
